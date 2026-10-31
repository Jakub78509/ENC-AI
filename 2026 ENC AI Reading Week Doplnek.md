# **🚀 Proč záleží na Teorii her v reálném inženýrství?**

V realné práci často v globálních technologických gigantech a startupovém prostředí opakovaně narazíte na ten samý scénář: inženýři navrhnou technicky dokonalý architekturu distribuovaného systému, ale celý systém zkolabuje ve chvíli, kdy do něj vstoupí reální autonomní aktéři (uživatelé, konkurenční algoritmy, automatizovaní boti nebo nezávislé mikroslužby).

Když stavíte systémy pro reálný svět – ať už jde o autonomní cenotvorbu na e-commerce platformách, aukční systémy pro digitální reklamu v reálném čase (RTB), alokaci zdrojů v cloudu nebo zabezpečení blockchainových sítí \- **neřešíte pouze algoritmy vyhledávání, ale strategickou interakci více agentů**.

Tento doprovodný materiál propojuje koncepty z vašeho **Student Lab Workbooku** s reálnou architekturou produkčních AI systémů.

## **1\. Nashova rovnováha, Minimax a Vězňovo dilema v produkci**

### **Reálný Use Case: Algoritmické cenové války na E-commerce trzích**

Představte si dva velké internetové prodejce (např. Amazon a Walmart), kteří používají automatizované boty pro dynamickou úpravu cen identického zboží:

* Pokud oba prodejci drží **vysokou cenu** (Spolupráce $C$), oba dosahují vysoké marže ($3$ body / zisk).  
* Pokud jeden prodejce cenu **sníží** (Zrada $D$) a druhý ji podrží ($C$), ten, který zlevnil, přetáhne zákazníky a získá maximální podíl na trhu ($5$ bodů), zatímco druhý realizuje nulový prodej ($0$ bodů).  
* Pokud **oba zlevní** ($D,D$), marže se rozpadne a oba získají pouze minimální zisk ($1$ bod).

### **Propojení s koncepty**

* **Nashova rovnováha:** Přestože by byl profil $(C,C)$ pro oba obchodníky výhodnější, jednostranná odchylka na $D$ vždy přináší vyšší okamžitý zisk ($5>3$ i $1>0$). Výsledná rovnováha v jednorázovém nastavení skončí v bodě $(D,D)$ – tedy v maržové spirále smrti.  
* **Minimax v Kybernetické bezpečnosti:** Když bezpečnostní inženýr navrhuje ochranu protokolu proti DDoS útokům nebo rate-limiting, nepředpokládá, že útočník bude spolupracovat. Aplikuje **Minimax** – předpokládá nejhorší možný scénář (útočník volí $D$) a volí takové nastavení své infrastruktury, které garantuje maximální možné minimum dostupné výpočetní kapacity.

💡 **Startup Insight:** Většina neúspěšných startupů v oblasti automatizovaného gamingu nebo B2B marketplaces selže proto, že předpokládá "hodné uživatele" ($C$). Úspěšné platfomy architektují systém s vědomím, že Nashova rovnováha účastníky přirozeně tlačí ke zradě ($D$), pokud systém nenavrhnou jako opakovanou (iterovanou) hru.

### **❓ Kontrolní otázky – Celkem 1: Teorie her v produkci**

**1\. Proč je v jednorázovém Vězňově dilematu profil $(D,D)$ jedinou Nashovou rovnováhou, přestože $(C,C)$ přináší vyšší celkový užitek?**

**Odpověď:**&nbsp;

&nbsp;

**2\. V jakém produkčním scénáři distribuovaných systémů je vhodné použiít rozhodování podle principu Minimax místo očekávané utility?**

**Odpověď:**&nbsp;

&nbsp;

**3\. Jaký je zásadní rozdíl mezi Pareto optimalitou a Nashovou rovnováhou ve Vězňově dilematu?**

**Odpověď:**&nbsp;

&nbsp;

## **2\. Evoluční dynamika populací a ESS v praxi**

### **Reálný Use Case: Ekosystém finančních botů a detekce podvodů**

Představte si trh decentralizovaných financí (DeFi) nebo vysokofrekvenčního obchodování (HFT).&nbsp;  
V systému působí populace tisíců nezávislých obchodních botů:

* Boti hrající naivní strategie (např. trvalé poskytování liquidity bez ochrany \= Always Cooperate) jsou v populaci po vystavení predatory algoritmům (Arbitrage / MEV bots \= Always Defect) okamžitě vyčerpáni.  
* Kapitál (fitness) se přelévá k dravým botům. Nicméně ve chvíli, kdy predatory boti vyhubí naivní poskytovatele liquidity, začnou soupeřit mezi sebou, čímž jejich průměrný výnos klesá k $1$ bodu (vzájemný boj o milisekundy).  
* Vítězem se stávají adaptivní strategie typu Tit-for-Tat nebo Win-Stay Lose-Shift, které mezi sebou dokáží bezpečně kooperovat za $3$ body, ale dravce okamžitě izolují a penalizují.

```

[Generace 0]                       [Generace 5]                      [Generace 15]
Dominují: AC (Naivní)              Expanze: AD (Dravci)               Stabilita: TFT / Adaptivní
┌──────────────────┐               ┌──────────────────┐               ┌──────────────────┐
│ AC   AC   AC   AC│  Invaze AD    │ AD   AD   AD   AD│  Samo-destrukce  │ TFT  TFT  TFT  TFT│
│ AC   AC   AC   AD│ ────────────► │ AD   AD   AC   TFT│ ──────────────►  │ TFT  TFT  TFT  AD │
└──────────────────┘               └──────────────────┘               └──────────────────┘
 AC hynou na exploataci             AD zápasí mezi sebou               TFT tvoří stabilní ESS

```

&nbsp;

Propojení s koncepty

* **Ruletová selekce (Fitness Proportional Selection):** V evolučním simulátoru simulation.py reprezentuje tržní mechanismus – strategie s vyšším kumulovaným skóre získávají v další generaci větší podíl výpočetní kapacity / kapitálu.  
* **Evolučně stabilní strategie (ESS):** Strategie je ESS, pokud ji malá skupina "invazivních" mutantů (např. jednotky $AD$ v populaci $TFT$) nedokáže přetlačit a postupně v populaci vymře.

&nbsp;

💡 **Startup Insight:** Pokud navrhujete otevřený API ekosystém (např. vývojářskou platformu), musíte počítat s tím, že parazitní agenty ($AD$) se objeví okamžitě. Váš systém musí mít v sobě zabudovaný mechanismus "reputace" (princip *Tit-For-Tat*), aby populace poctivých vývojářů nevymřela.

&nbsp;

### **❓ Kontrolní otázky – Celkem 2: Evoluční dynamika populací**

**1\. Proč čistě spolupracující populace látek (Always Cooperate) nepředstavuje Evolučně stabilní strategii (ESS)?**

**Odpověď:**&nbsp;

&nbsp;

**2\. Jakým způsobem simuluje ruletová selekce v programu** simulation.py **tržní prostředí?**

**Odpověď:**&nbsp;

&nbsp;

**3\. Proč strategie *Tit-for-Tat* dokáže na rozdíl od *Always Cooperate* zastavit invazi zrádců (Always Defect)?**

**Odpověď:**&nbsp;

&nbsp;

## **3\. Změna pravidel hry: Od Vězňova dilematu ke Stag Hunt**

### **Reálný Use Case: Přechod na odvětvové technické standardy**

Uvažujme dvě technologické firmy rozhodující se o architektuře svém dalším produktu:

* **Option C (Společný otevřený standard, např. Kubernetes / OpenTelemetry / USB-C):** Vyžaduje koordinované investice, ale přináší obrovský synergický efekt pro celý trh.  
* **Option D (Vlastní uzavřené řešení / Lock-in):** Bezpečnější izolovaná cesta s garantovaným malým ziskem.

Pokud změníme matici odměn na hru **Stag Hunt (Lov na jelena)** s podmínek $R>T>P>S$:

* Oboustranný lov jelena ($C,C$) přináší **nejvyšší zisk** ($4$ body).  
* Samostatný lov zajíce ($D$) garantuje stabilních $3$ body bez ohledu na soupeře.  
* Pokud se jedinec pokusí ulovit jelena sám ($C$), zatímco druhý uteče lovit zajíce ($D$), nezíská nic ($0$ bodů).

### **Propojení s koncepty**

* **Změna struktury odměn:** Když $R>T$, zrada už není striktně dominantní akcí\!  
* **Koordinační hra se dvěma Nashovými rovnováhami:** V hře Stag Hunt existují dvě ryzí Nashovy rovnováhy: $(C,C)$ – sociálně optimální, a $(D,D)$ – riskantně bezpečná.

```

      VĚZŇOVO DILEMA (T > R > P > S)                 STAG HUNT (R > T > P > S)
      Dominantní strategie = Zrada (D)              Koordinační hra (2 Nash equilibria)
      
          C             D                               C             D
     ┌───────────┬───────────┐                     ┌───────────┬───────────┐
   C │   3 / 3   │   0 / 5   │                   C │ **4 / 4** │   0 / 3   │
     ├───────────┼───────────┤                     ├───────────┼───────────┤
   D │   5 / 0   │ **1 / 1** │                   D │   3 / 0   │ **1 / 1** │
     └───────────┴───────────┘                     └───────────┴───────────┘

```

&nbsp;

💡 **Startup Insight:** Většina zakladatelů si myslí, že hraje Vězňovo dilema, ale ve skutečnosti buduje ekosystémy typu Stag Hunt. V Stag Hunt nevyhrává ten, kdo ostatní podvede, ale ten, kdo dokáže vybudovat **důvěru a signalizační protokol**, který celou populaci posune z bezpečné podoptimální rovnováhy $(D,D)$ do vysoce ziskové koordinační rovnováhy $(C,C)$.

### **❓ Kontrolní otázky – Celkem 3: Změna matice odměn**

**1\. Jaká klíčová změna v nerovnostech matice odměn transformuje hraniční stabilitu Vězňova dilemata na hru Stag Hunt?**

**Odpověď:**&nbsp;

&nbsp;

**2\. Kolik ryzích Nashových rovnováh existuje v hře Stag Hunt a které to jsou?**

**Odpověď:**&nbsp;

&nbsp;

**3\. Proč vyžaduje přechod populace do koordinační rovnováhy $(C,C)$ v hře Stag Hunt explicitní komunikaci nebo signalizaci?**

**Odpověď:**&nbsp;

&nbsp;

## **4\. Adaptivní a učící se agenti: Q-Learning v Multiagentním prostředí**

### **Reálný Use Case: Boti pro automatické dražby reklam (RTB platforms)**

Představte si platformu Google Ad Exchange nebo Meta Ads, kde milióny $Q$\-learning botů v reálném čase draží zobrazení reklamy:

* Prostředí je **nestacionární** – zatímco se bot učí nejlepší strategii, ostatní boti v populaci se také učí a mění své chování\!  
* Pevná pravidla (např. Tit-for-Tat) v tomto prostředí selhávají, protože nedokáží optimálně reagovat na komplexní sekvence nabídek.  
* $Q$\-learning agent si vytváří vnitřní reprezentaci stavu $s$ (historie předchozích kroků) a postupně aktualizuje matici $Q(s,a)$, která vyjadřuje očekávaný dlouhodobý zisk pro akci $a\in \{C,D\}$.

```

 Vstupní stav (s)            Epsilon-Greedy Výběr                    Aktualizace Q-tabulky
┌──────────────────┐        ┌───────────────────────┐        ┌──────────────────────────────────────┐
│ Poslední tah     │ ─────► │ random() < ε ?        │ ─────► │ Q(s,a) 🡸 Q(s,a) + α[r + γ max Q(s') - Q(s,a)]
│ soupeře = 'D'    │        │  ➜ Exploration (Náhod)│        └──────────────────────────────────────┘
└──────────────────┘        │  ➜ Exploitation (Argmax)       Postupná adaptace na populaci oponentů
                            └───────────────────────┘

```

Propojení s koncepty

* **Průzkum vs. Exploatace ($\epsilon$\-greedy):** Parametr $\epsilon$ určuje, jak často agent zkouší náhodný tah pro objevení reakcí soupeře vs. jak často volí aktuálně nejlepší známý tah.  
* **Non-stationarity problem:** V klasickém zpětnovazebním učení (RL) je prostředí stacionární. V multiagentním prostředí se však "prostředí" skládá z ostatních učících se agentů, což je největší výzva při nasazování AI agentů v produkci.

&nbsp;

💡 **Startup Insight:** Pokud v praxi stavíte víceagentní AI systém (např. síť autonomních doručovacích dronů nebo energetických mikro-sítí), nepoužívejte deterministická pravidla. Použijte zpětnovazební učení, ale nezapomeňte správně dimenzovat parametrický útlum učení ($\alpha$), jinak váš systém v populaci neusadí v žádné rovnováze a bude neustále oscilovat.

### 

### **❓ Kontrolní otázky – Celkem 4: Multi-agentní Q-Learning**

**1\. Co reprezentuje stav $s$ u jednoduchého $Q$\-learning agenta hrajícího iterované Vězňovo dilema?**

**Odpověď:**&nbsp;

&nbsp;

**2\. Jaký vliv má parametr exploration ($\epsilon$) v $\epsilon$\-greedy strategii na dlouhodobou stabilitu v multiagentní simulaci?**

**Odpověď:**&nbsp;

&nbsp;

**3\. Proč je výcvik $Q$\-learning agenta v populaci jiných učících se agentů (multiagentní prostředí) podstatně náročnější než výcvik v klasickém stacionárním prostředí?**

**Odpověď:**&nbsp;

&nbsp;

&nbsp;