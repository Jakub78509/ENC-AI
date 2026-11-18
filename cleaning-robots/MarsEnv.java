import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Prostředí "Mars" z příkladu cleaning-robots, zobecněné na více robotů.
 *
 * Původní verze znala jen r1 (uklízeč) a r2 (spalovač). Tahle verze dostane
 * z mars.mas2j počet uklízečů a spalovačů a roboty rozliší podle jména:
 *   cleaner1..cleanerN  -> id 0..N-1
 *   burner1..burnerM    -> id N..N+M-1
 * (Jason pojmenuje agenty "cleaner #3" jako cleaner1, cleaner2, cleaner3;
 *  pokud je instance jen jedna, jmenuje se agent prostě "cleaner".)
 */
public class MarsEnv extends Environment {

    public static final int GSize = 7;  // velikost mřížky
    public static final int GARB  = 16; // kód odpadku v modelu mřížky

    public static final Term ns = Literal.parseLiteral("next(slot)");
    public static final Term pg = Literal.parseLiteral("pick(garb)");
    public static final Term dg = Literal.parseLiteral("drop(garb)");
    public static final Term bg = Literal.parseLiteral("burn(garb)");

    /** Startovní pozice spalovačů (použije se prvních nBurners). */
    static final int[][] BURNER_POS = { {3, 3}, {1, 5}, {5, 1}, {5, 5}, {1, 1} };

    static Logger logger = Logger.getLogger(MarsEnv.class.getName());

    private int nCleaners = 1;
    private int nBurners  = 1;

    private MarsModel model;
    private MarsView  view;

    @Override
    public void init(String[] args) {
        // environment: MarsEnv(<počet uklízečů>, <počet spalovačů>)
        if (args.length >= 1) nCleaners = Integer.parseInt(args[0].trim());
        if (args.length >= 2) nBurners  = Integer.parseInt(args[1].trim());
        if (nBurners > BURNER_POS.length)
            throw new IllegalArgumentException("Maximálně " + BURNER_POS.length + " spalovačů");

        model = new MarsModel();
        view  = new MarsView(model);
        model.setView(view);
        updatePercepts();
    }

    // ---------------------------------------------------------------- jména <-> id

    boolean isCleaner(int id) { return id < nCleaners; }

    String agName(int id) {
        if (isCleaner(id))
            return nCleaners == 1 ? "cleaner" : "cleaner" + (id + 1);
        int j = id - nCleaners;
        return nBurners == 1 ? "burner" : "burner" + (j + 1);
    }

    int agId(String name) {
        for (int id = 0; id < nCleaners + nBurners; id++)
            if (agName(id).equals(name)) return id;
        return -1;
    }

    // ---------------------------------------------------------------- akce

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag + " doing: " + action);
        int id = agId(ag);
        if (id < 0) {
            logger.warning("Neznámý agent " + ag);
            return false;
        }

        synchronized (model) {
            try {
                if (action.equals(ns) && isCleaner(id)) {
                    model.nextSlot(id);
                } else if (action.getFunctor().equals("move_towards") && isCleaner(id)) {
                    int x = (int) ((NumberTerm) action.getTerm(0)).solve();
                    int y = (int) ((NumberTerm) action.getTerm(1)).solve();
                    model.moveTowards(id, x, y);
                } else if (action.equals(pg) && isCleaner(id)) {
                    model.pickGarb(id);
                } else if (action.equals(dg) && isCleaner(id)) {
                    model.dropGarb(id);
                } else if (action.equals(bg) && !isCleaner(id)) {
                    model.burnGarb(id);
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            updatePercepts();
        }

        try {
            Thread.sleep(200);
        } catch (Exception e) {}
        informAgsEnvironmentChanged();
        return true;
    }

    /**
     * updatePercepts() vjemy nejdřív smaže a pak znovu přidá. Kdyby si je agent
     * přečetl mezi tím, chyběly by mu (např. pos(burner1,..)) a jeho plán by selhal.
     * S více roboty se to reálně stává, proto čtení i aktualizace běží pod zámkem modelu.
     */
    @Override
    public java.util.Collection<Literal> getPercepts(String agName) {
        if (model == null) return super.getPercepts(agName);
        synchronized (model) {
            return super.getPercepts(agName);
        }
    }

    /** Vjemy (percepts) jsou společné pro všechny agenty. */
    void updatePercepts() {
        clearPercepts();
        for (int id = 0; id < nCleaners + nBurners; id++) {
            String name = agName(id);
            Location loc = model.getAgPos(id);
            addPercept(Literal.parseLiteral("pos(" + name + "," + loc.x + "," + loc.y + ")"));
            if (!isCleaner(id))
                addPercept(Literal.parseLiteral("burner(" + name + ")"));
            if (model.hasObject(GARB, loc))
                addPercept(Literal.parseLiteral("garbage(" + name + ")"));
        }
    }

    // ---------------------------------------------------------------- model

    class MarsModel extends GridWorldModel {

        public static final int MErr = 2; // max. počet neúspěšných pokusů o sebrání
        int[]     nerr    = new int[nCleaners];     // počet pokusů o pick per uklízeč
        boolean[] hasGarb = new boolean[nCleaners]; // nese uklízeč odpadek?
        int[]     steps   = new int[nCleaners];     // kolik políček už prošel

        Random random = new Random(System.currentTimeMillis());

        private MarsModel() {
            super(GSize, GSize, nCleaners + nBurners);

            try {
                // uklízeči startují v levém sloupci, rovnoměrně rozložení po řádcích
                for (int i = 0; i < nCleaners; i++)
                    setAgPos(i, 0, i * GSize / nCleaners);
                // spalovači stojí na pevných místech
                for (int j = 0; j < nBurners; j++)
                    setAgPos(nCleaners + j, BURNER_POS[j][0], BURNER_POS[j][1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // počáteční rozmístění odpadků
            add(GARB, 3, 0);
            add(GARB, GSize - 1, 0);
            add(GARB, 1, 2);
            add(GARB, 0, GSize - 2);
            add(GARB, GSize - 1, GSize - 1);
        }

        /** Překreslí ostatní agenty (když přes ně někdo přejde, zmizeli by z view). */
        void redrawOthers(int except) {
            for (int id = 0; id < nCleaners + nBurners; id++)
                if (id != except) setAgPos(id, getAgPos(id));
        }

        /** Projde mřížku po řádcích (s přetečením na začátek), celkem jedno kolo. */
        void nextSlot(int id) {
            if (steps[id] >= GSize * GSize - 1) return; // prošel už celou mřížku
            Location r = getAgPos(id);
            r.x++;
            if (r.x == getWidth()) {
                r.x = 0;
                r.y = (r.y + 1) % getHeight();
            }
            steps[id]++;
            setAgPos(id, r);
            redrawOthers(id);
        }

        void moveTowards(int id, int x, int y) {
            Location r = getAgPos(id);
            if (r.x < x) r.x++;
            else if (r.x > x) r.x--;
            if (r.y < y) r.y++;
            else if (r.y > y) r.y--;
            setAgPos(id, r);
            redrawOthers(id);
        }

        void pickGarb(int id) {
            if (hasGarb[id] || !hasObject(GARB, getAgPos(id))) return;
            // sebrání občas selže, ale nikdy víc než MErr-krát po sobě
            if (random.nextBoolean() || nerr[id] == MErr) {
                remove(GARB, getAgPos(id));
                nerr[id] = 0;
                hasGarb[id] = true;
            } else {
                nerr[id]++;
            }
        }

        void dropGarb(int id) {
            if (hasGarb[id]) {
                hasGarb[id] = false;
                add(GARB, getAgPos(id));
            }
        }

        void burnGarb(int id) {
            if (hasObject(GARB, getAgPos(id)))
                remove(GARB, getAgPos(id));
        }
    }

    // ---------------------------------------------------------------- vizualizace

    class MarsView extends GridWorldView {

        public MarsView(MarsModel model) {
            super(model, "Mars World", 600);
            defaultFont = new Font("Arial", Font.BOLD, 18);
            setVisible(true);
            repaint();
        }

        @Override
        public void draw(Graphics g, int x, int y, int object) {
            if (object == MarsEnv.GARB) drawGarb(g, x, y);
        }

        @Override
        public void drawAgent(Graphics g, int x, int y, Color c, int id) {
            String label;
            if (isCleaner(id)) {
                label = "C" + (id + 1);
                c = Color.yellow;
                if (((MarsModel) model).hasGarb[id]) {
                    label += "-G";
                    c = Color.orange;
                }
            } else {
                label = "B" + (id - nCleaners + 1);
                c = Color.blue;
            }
            super.drawAgent(g, x, y, c, -1);
            g.setColor(isCleaner(id) ? Color.black : Color.white);
            super.drawString(g, x, y, defaultFont, label);
            repaint();
        }

        public void drawGarb(Graphics g, int x, int y) {
            super.drawObstacle(g, x, y);
            g.setColor(Color.white);
            drawString(g, x, y, defaultFont, "G");
        }
    }
}
