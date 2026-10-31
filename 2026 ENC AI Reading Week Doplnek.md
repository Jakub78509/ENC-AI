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

**Odpověď:** Protože zrada $D$ je pro oba hráče **dominantní strategie**. Ať soupeř zvolí cokoliv, $D$ přinese víc: proti $C$ je $5 > 3$, proti $D$ je $1 > 0$. V profilu $(D,D)$ si tedy nikdo jednostrannou změnou nepomůže (přechod na $C$ by snížil zisk z 1 na 0), což je definice Nashovy rovnováhy. V $(C,C)$ má naopak každý motivaci odchýlit se na $D$ ($5 > 3$), takže $(C,C)$ rovnováhou není. Vyšší společný užitek $(C,C)$ by vyžadoval závaznou dohodu nebo opakovanou hru s možností odvety, kterou jednorázová hra nemá.

&nbsp;

**2\. V jakém produkčním scénáři distribuovaných systémů je vhodné použiít rozhodování podle principu Minimax místo očekávané utility?**

**Odpověď:** Všude, kde nelze předpokládat spolupracujícího ani „průměrného“ aktéra a kde selhání nejhoršího případu má kritické následky. Jde hlavně o **adversariální prostředí**: ochrana proti DDoS a rate-limiting, návrh konsenzu odolného vůči byzantským (zlomyslným) uzlům, bezpečnostní protokoly a autentizace, dimenzování kapacity pro špičky a výpadky (SLA). Očekávaná utilita potřebuje známé pravděpodobnosti chování ostatních a optimalizuje průměr. Útočník ale cíleně volí to nejhorší, takže je správné maximalizovat garantované minimum.

&nbsp;

**3\. Jaký je zásadní rozdíl mezi Pareto optimalitou a Nashovou rovnováhou ve Vězňově dilematu?**

**Odpověď:** **Pareto optimalita** je kolektivní kritérium: nelze nikomu přilepšit, aniž by si jiný pohoršil. Ve Vězňově dilematu jsou Pareto optimální $(C,C)$, $(C,D)$ i $(D,C)$, jen $(D,D)$ není, protože $(C,C)$ je pro oba lepší. **Nashova rovnováha** je individuální kritérium stability: nikdo si nepomůže jednostrannou změnou. Ve Vězňově dilematu je jí jen $(D,D)$. Obě množiny jsou tedy **disjunktní**: jediná stabilní situace je zároveň jediná Pareto neoptimální. Racionální sobecké jednání jednotlivců vede ke kolektivně špatnému výsledku, a to je podstata dilematu.

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

**Odpověď:** Protože ji snadno napadne mutant `Always Defect`. Ten proti AC získává $T = 5$ za kolo, zatímco AC mezi sebou jen $R = 3$, takže má AD vyšší fitness a selekce ho rozšíří. AC nemá žádný mechanismus odvety ani rozpoznání zrádce. Simulace to potvrdila: z 45× AC + 5× AD zbylo za 10 generací 50× AD. ESS musí mít proti sobě samé vyšší (nebo stejný a proti mutantovi vyšší) zisk než mutant, a to AC nesplňuje.

&nbsp;

**2\. Jakým způsobem simuluje ruletová selekce v programu** simulation.py **tržní prostředí?**

**Odpověď:** Každý jedinec odehraje sérii zápasů a jeho kumulované skóre funguje jako **fitness (kapitál)**. Pravděpodobnost, že se jedinec dostane do další generace, je úměrná podílu na celkovém skóre: $P_i = \text{Skóre}_i / \sum_j \text{Skóre}_j$. To odpovídá trhu, kde úspěšné strategie získávají větší podíl kapitálu, zákazníků nebo výpočetní kapacity a neúspěšné postupně mizí. Výběr je ale náhodný (s opakováním), takže i slabší strategie může chvíli přežívat a silná může náhodou ztratit (genetický drift / tržní šum).

&nbsp;

**3\. Proč strategie *Tit-for-Tat* dokáže na rozdíl od *Always Cooperate* zastavit invazi zrádců (Always Defect)?**

**Odpověď:** Protože TFT je **podmíněná spolupráce s odvetou**: začne $C$ a pak kopíruje poslední tah soupeře. Proti zrádci ztratí jen první kolo a dál hraje $D$, takže AD z TFT vytěží jen ~204 bodů za 200 kol, zatímco dvojice TFT–TFT získá 600. Zrádce v populaci TFT tedy má nižší fitness než domácí strategie a vymírá. AC naopak nechá zrádce vykořisťovat se donekonečna (0 vs. 5 v každém kole). V simulaci 45× TFT + 5× AD měli AD v generaci 0 průměr 4 359 bodů oproti 12 358 u TFT a vymřeli.

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

**Odpověď:** Prohození pořadí $T$ a $R$. Ve Vězňově dilematu platí $T > R > P > S$ (pokušení zradit je větší než odměna za spolupráci), ve Stag Hunt $R > T > P > S$. Když se spolupráce vyplatí víc než jednostranná zrada, $D$ přestane být dominantní strategií. Nejlepší odpověď na $C$ je $C$ a na $D$ je $D$, takže z hry s jednou rovnováhou vznikne koordinační hra.

&nbsp;

**2\. Kolik ryzích Nashových rovnováh existuje v hře Stag Hunt a které to jsou?**

**Odpověď:** **Dvě:** $(C,C)$, tedy společný lov jelena, který je Pareto optimální a sociálně nejlepší (4/4), a $(D,D)$, tedy lov zajíce, který je rizikově dominantní a bezpečný (1/1). V žádné z nich si hráč jednostrannou změnou nepomůže: z $(C,C)$ by odchylkou získal $3 < 4$, z $(D,D)$ by získal $0 < 1$. Kromě nich existuje ještě jedna smíšená rovnováha.

&nbsp;

**3\. Proč vyžaduje přechod populace do koordinační rovnováhy $(C,C)$ v hře Stag Hunt explicitní komunikaci nebo signalizaci?**

**Odpověď:** Protože obě rovnováhy jsou stabilní a hráč volí $C$ jen tehdy, když věří, že soupeř zvolí také $C$. Jinak riskuje nejhorší výsledek $S = 0$, zatímco $D$ je bezpečnější (Minimax volí $D$). Bez sdílené informace racionální hráči sklouznou k rizikově dominantní $(D,D)$. Komunikace, signalizace, reputace nebo závazky (např. veřejný závazek k otevřenému standardu) vytvoří **společné očekávání**, které snižuje riziko a umožní koordinaci na $(C,C)$. Ve Stag Hunt nikdo nemá motivaci lhát, protože $(C,C)$ je nejlepší pro oba, takže signál je důvěryhodný.

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

**Odpověď:** Stav je stručná paměť o průběhu hry, typicky **poslední tah soupeře**. V našem agentovi jsou stavy `START` (první kolo), `C` (soupeř naposledy spolupracoval) a `D` (soupeř naposledy zradil). Pro každý stav agent drží Q-hodnoty akcí $C$ a $D$, tedy odhad dlouhodobého diskontovaného zisku. Stav lze rozšířit o vlastní poslední tah nebo delší historii (např. 4 stavy $CC, CD, DC, DD$), aby agent lépe rozpoznal, jestli soupeř oplácí.

&nbsp;

**2\. Jaký vliv má parametr exploration ($\epsilon$) v $\epsilon$\-greedy strategii na dlouhodobou stabilitu v multiagentní simulaci?**

**Odpověď:** $\epsilon$ určuje podíl náhodných tahů. Vyšší $\epsilon$ pomáhá objevovat reakce soupeřů a reagovat na změny populace, ale v IPD každá náhodná zrada vyvolá odvetu reciprokých strategií (TFT) a může spustit sérii vzájemných zrad. Agent pak ztrácí body a populace osciluje. Nízké $\epsilon$ přináší stabilitu, ale agent se může zaseknout v podoptimální strategii naučené dříve (např. zrada, když bylo na začátku hodně AD). V praxi se proto používá **klesající $\epsilon$** (z vysokého k téměř nulovému), aby se systém nejprve rozhlédl a pak ustálil v rovnováze.

&nbsp;

**3\. Proč je výcvik $Q$\-learning agenta v populaci jiných učících se agentů (multiagentní prostředí) podstatně náročnější než výcvik v klasickém stacionárním prostředí?**

**Odpověď:** Kvůli **nestacionaritě**. Klasický Q-learning předpokládá, že prostředí (přechody a odměny) je pevné, a za těchto podmínek má zaručenou konvergenci. V multiagentním systému je ale „prostředí“ tvořeno ostatními agenty, kteří se současně učí a mění chování. Optimální odpověď se tak průběžně posouvá, staré zkušenosti zastarávají a Markovská vlastnost neplatí. Agenti se mohou navzájem „honit“ a oscilovat bez dosažení rovnováhy. Navíc je stavový prostor kvůli kombinaci akcí všech agentů výrazně větší a problematické je přiřazení zásluh (credit assignment). V našem experimentu se agent nejprve naučil zrazovat proti převaze AD, a když AD vymřeli, TFT ho za stejné chování trestal.

&nbsp;

&nbsp;