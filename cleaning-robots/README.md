# Seminář 07 – BDI agenti v AgentSpeak a Jason

Řešení cvičení nad příkladem `cleaning-robots` z distribuce Jasonu (verze **3.3.3**).

## Spuštění

Jason 3.3.3 potřebuje **Javu 21**. Při prvním spuštění si sám stáhne Gradle a přeloží `MarsEnv.java`.

```bash
sdk use java 21.0.5-tem
cd cleaning-robots
<cesta-k-jasonu>/bin/jason mars.mas2j
```

Otevřou se dvě okna: **Mars World** (mřížka 7×7) a **MAS Console** (log akcí agentů).

Prostředí bylo místo VS Code/jEdit vyvíjeno v IntelliJ IDEA. Jason 3.2+ se spouští z příkazové řádky,
takže na editoru nezáleží (bod checklistu o nastavení jEdit se týká jen starších verzí Jasonu).

## Struktura

| Soubor | Obsah |
|---|---|
| `mars.mas2j` | popis MAS: prostředí `MarsEnv(3, 2)`, agenti `cleaner #3`, `burner #2` |
| `MarsEnv.java` | prostředí (model mřížky, akce, vjemy, vizualizace) |
| `cleaner.asl` | uklízecí robot (původně `r1.asl`) |
| `burner.asl` | spalovací robot (původně `r2.asl`) |

Ve vizualizaci jsou uklízeči žlutí `C1`–`C3` (oranžoví `Cx-G`, když nesou odpadek), spalovači modří `B1`, `B2`.

---

## Architektura prostředí (část 3)

- **`init(String[] args)`** – volá se jednou při startu MAS. Vytvoří model (`MarsModel`, potomek
  `GridWorldModel`) a view, rozmístí agenty a odpadky a nastaví počáteční vjemy voláním `updatePercepts()`.
  `args` jsou parametry z `.mas2j` – v naší verzi `MarsEnv(3, 2)` = počet uklízečů a spalovačů.
- **`executeAction(String ag, Structure action)`** – volá se pokaždé, když agent v těle plánu provede akci
  prostředí. `ag` je jméno agenta, `action` je term akce. Akce se rozliší porovnáním s předpřipravenými
  termy (`action.equals(ns)` pro `next(slot)`) nebo podle funktoru (`action.getFunctor().equals("move_towards")`);
  parametry se čtou přes `action.getTerm(i)` a převádějí `((NumberTerm) ...).solve()`. Návratová hodnota
  říká, zda akce uspěla (`false` → akce v plánu agenta selže).
- **`updatePercepts()`** – volá se na konci `init` a v `executeAction` po každé provedené akci. Smaže
  vjemy (`clearPercepts()`) a podle stavu modelu přidá nové: `pos(Agent,X,Y)`, `garbage(Agent)` a (nově)
  `burner(Agent)`. Následně `informAgsEnvironmentChanged()` agenty probudí, aby si vjemy přečetli.

## Praktické úkoly (část 5)

### Úkol 1 – další spalovací robot
- V `mars.mas2j` je `burner #2` (Jason vytvoří agenty `burner1`, `burner2`).
- Původní `MarsEnv` měl napevno indexy `0 = r1`, `1 = r2`. Nově se agenti mapují podle jména
  (`cleanerN → id N-1`, `burnerM → id nCleaners+M-1`) a model pracuje s poli (`hasGarb[]`, `nerr[]`, `steps[]`).
- Spalovače stojí na `(3,3)` a `(1,5)`. Akce `burn(garb)` spálí odpadek na pozici toho spalovače, který ji volá.
- `burner.asl` je obecný: `+garbage(Me) : .my_name(Me) <- burn(garb).`

### Úkol 2 – nošení k nejbližšímu spalovači
V `cleaner.asl`:

```prolog
dist(X1,Y1,X2,Y2,D) :- D = math.max(math.abs(X1-X2), math.abs(Y1-Y2)).

nearest_burner(B) :-
    .my_name(Me) & pos(Me,X,Y) &
    .findall(d(D,Bn), burner(Bn) & pos(Bn,BX,BY) & dist(X,Y,BX,BY,D), L) &
    .min(L, d(_,B)).
```

`move_towards` posouvá robota v ose x i y zároveň (umí diagonálu), proto je počet kroků Čebyševova
vzdálenost `max(|dx|, |dy|)`. Plán `+!carry_garbage` po sebrání odpadku zavolá `?nearest_burner(B)` a jde k `B`.

Ověřeno z logu – odpadek z `(0,5)` nese uklízeč k `burner2` na `(1,5)` (vzdálenost 1 vs. 3),
ostatní (`(3,0)`, `(6,0)`, `(1,2)`, `(6,6)`) k `burner1` na `(3,3)`.

### Úkol 3 – více uklízecích robotů
- `cleaner #3`; uklízeči startují v levém sloupci v řádcích 0, 2, 4 a procházejí mřížku po řádcích
  (s přetečením na začátek), každý jedno celé kolo.
- Agent místo pevného `r1` používá `.my_name(Me)`, takže stejný kód běží ve všech instancích.

**Pozorování:**
- Úklid je výrazně rychlejší – odpadky se sbírají paralelně (každý uklízeč uklidil 1–2 odpadky).
- Agenti spolu nekomunikují ani se nekoordinují: každý prochází celou mřížku, takže velkou část
  práce dělají zbytečně dvakrát; dva uklízeči se mohou sejít u stejného odpadku (jeden ho sebere,
  druhý pak „nese“ nic). Rozumné rozšíření by bylo rozdělit si mřížku (např. přes `.send`).
- Roboti se mohou ocitnout na stejném políčku – `GridWorldView` pak vykreslí jen jednoho z nich.
- Odpadek položený na políčko spalovače nesmí jiný uklízeč sebrat znovu – proto pravidlo `on_burner`.
- **Souběh (race condition):** s více agenty se projevila chyba, kterou původní příklad s jedním
  robotem prakticky neukázal – `updatePercepts()` vjemy nejdřív smaže a pak přidá, a agent, který si je
  přečetl mezi tím, neviděl např. `pos(burner1,…)`; test `?pos(L,X,Y)` selhal a robot se zasekl.
  Opraveno přepsáním `getPercepts()` tak, aby čtení i aktualizace vjemů běžely pod zámkem modelu.

---

## Závěrečné otázky

**1. Jak Jason odlišuje akce prostředí od interních akcí agenta?**
Syntakticky **tečkou**. Akce prostředí se píšou bez prefixu (`pick(garb)`, `move_towards(X,Y)`) a interpret
je předá prostředí – zavolá `executeAction`. Interní akce začínají tečkou (`.print(...)`, `.my_name(Me)`,
`.findall(...)`) a provádí je přímo interpret uvnitř agenta, bez účasti prostředí. Uživatelské interní
akce v Javě se zapisují s názvem balíčku, např. `mylib.myaction(...)`.

**2. K čemu slouží kontext plánu a co se stane, když není splněn?**
Kontext (část za `:`) je logická podmínka nad belief base, která rozhoduje, zda je **relevantní** plán
(sedí trigger) zároveň **aplikovatelný** v aktuální situaci. Umožňuje mít pro stejný trigger více plánů
pro různé okolnosti (např. `+!at(L) : at(L).` vs. `+!at(L) <- ... move_towards ...`). Jason vybere první
plán v pořadí, jehož kontext platí. Pokud kontext nesplňuje žádný plán:
- u **cíle** (`+!g`) cíl selže – vygeneruje se událost `-!g` (failure); když pro ni není plán, záměr
  (intention) se zahodí a Jason vypíše „No applicable plan“;
- u **změny belief** (`+b`) se událost prostě zahodí.

Proto má `cleaner.asl` i prázdný záložní plán `+!check(slots).` – aby cíl neselhal, když na políčku leží odpadek.

**3. Rozdíl mezi dotazovacím cílem `?` a ověřením belief v kontextu?**
Ověření v **kontextu** je pasivní dotaz do belief base v okamžiku výběru plánu; neúspěch jen znamená, že
plán není aplikovatelný (zkusí se jiný). **Dotazovací cíl** `?b` v těle plánu se nejdřív také zeptá
belief base, ale když odpověď nenajde, vygeneruje událost `+?b` a spustí **dotazovací plán**
(`+?b <- ...`), který může informaci aktivně zjistit – provést akce, přesunout se, zeptat se jiného
agenta (např. dojít k ledničce zjistit, jestli je tam pivo). Navíc navazuje proměnné, které se dál
používají v těle (`?pos(Me,X,Y)`). Pokud selže i to, selže celý plán, ve kterém je `?` použit.

## Sebereflexe

Nejobtížnější bylo přepnout z procedurálního myšlení („udělej krok 1, pak krok 2“) na reaktivní model,
kde se chování skládá z plánů spouštěných událostmi a interpret sám rozhoduje, který plán a kdy poběží.
Řídicí tok není vidět na jednom místě – například `+garbage(Me)` přeruší procházení mřížky voláním
`.drop_desire(check(slots))` z úplně jiného plánu. Neobvyklá je i prologovská unifikace místo přiřazení
a to, že chybějící plán se neprojeví výjimkou, ale tichým selháním cíle. Při více agentech se navíc
ukázalo, že paralelní běh přináší problémy (souběh při čtení vjemů), které s jedním agentem vůbec nevidíte.
