# Seminář 07 (Lab Worksheet): BDI Agenti v AgentSpeak a Jason

**Odhadovaná délka cvičení:** 80 minut
**Potřebné nástroje:** 
* Prostředí Java (JRE/JDK) nezbytné pro spuštění celého frameworku.
* Distribuce nástroje Jason (obsahuje interpret jazyka a vývojové prostředí jEdit).

**Výstupy učení:**
1. Schopnost konfigurovat vývojové prostředí jEdit pro projekty v jazyce AgentSpeak.
2. Analyzovat a interpretovat zdrojový kód BDI agentů.
3. Popsat strukturu multiagentního systému pomocí souboru `.mas2j`.
4. Navrhnout a implementovat úpravy chování agentů na základě vjemů a definovaných akcí.

---

## 1. Motivace a úvod do technologií

V tomto cvičení se zaměříme na implementaci inteligentních softwarových agentů. Seznámíte se s jazykem **AgentSpeak** a jeho interpretem **Jason**.

> Zkratka BDI (Belief-Desire-Intention) označuje architekturu, ve které má agent přesvědčení o světě (Beliefs), cíle, kterých chce dosáhnout (Desires), a konkrétní plány k vykonání (Intentions). **Více na přednášce**.

* **AgentSpeak** je interpretovaný programovací jazyk určený pro implementaci BDI agentů. Jeho syntaxe a fungování (např. proces unifikace) se velmi podobá jazyku Prolog.
* **Jason** je interpret tohoto jazyka, který navíc umožňuje snadno definovat jednoduchá prostředí pro simulace. 

---

## 2. Příprava vývojového prostředí a struktura projektu

Abyste mohli s Jasonem pracovat, je nutné využít správné vývojové prostředí.

Jsou dva způsoby, jak používat Jason, podle toho, jakou verzi Jasonu si stáhnete:

* Pro verze starší než Jason 3.2 distribuce Jasonu obsahuje verzi jEdit, která podporuje projekty AgentSpeak a Jason. Existuje také modul do Eclipse, který přidává podporu Jasonu (už není vyvíjen). Instrukce k instalaci modulu jsou dostupné na [jason.sourceforge.net](http://jason.sourceforge.net/mini-tutorial/eclipse-plugin/), nicméně doporučuji použít jEdit.
* Pro novější verze, můžete používat Visual Studio Code. Postup instalace je [popsaný v tutorialu k Jasonu](https://jason-lang.github.io/doc/tutorials/vscode/readme.html).


**Instrukce pro nastavení:**
1. Stáhněte si distribuci Jason z oficiálních stránek a rozbalte ji. [Dostupné zde](https://jason-lang.github.io/)
2. Spusťte přibalený editor **jEdit**, který projekty v AgentSpeaku nativně podporuje.
   * *Omezení:* Modul pro Eclipse pravděpodobně již není vyvíjen, proto se striktně držte prostředí jEdit.
3. **Typická chyba při spuštění:** Abyste mohli příklady spustit (pomocí zelené šipky v pravém dolním rohu), musíte nejprve nastavit cesty k běhovým knihovnám. V menu přejděte do `Plugins → Plugin Options → Jason` a nastavte cestu k souboru `jason.jar` a adresáři `ant-libs`.

### Průzkum konfiguračního souboru

**Zadání:** Otevřete příklad `cleaning-robots` (v adresáři `examples`) a najděte soubor `mars.mas2j`.

Jason je distribuován s několika příklady (v adresáři `examples`). Základy si ukážeme na příkladu `cleaning-robots`.

Tento příklad obsahuje dva roboty. Jeden z nich se pohybuje prostředím a sbírá odpadky, druhý sedí uprostřed a ničí všechny odpadky, které do něj zanesou ostatní roboti (přesněji řečeno, všechny odpadky, které jsou na stejném místě jako on). Prostředí je reprezentováno mřížkou 7x7 a poskytuje agentům několik základních akcí.

* `next(slot)` - přesune agenta na další pozici
* `moveTowards(x,y)` - posune agenta o jednu pozici blíže k daným souřadnicím
* `pick(garb)` - sebere odpad (může selhat)
* `drop(garb)` - položí odpad
* `burn(garb)` - spálí odpad na místě agenta `r2` (to je ten, který odpadky spaluje)

Prostředí také poskytuje následující informace

* `pos(R,X,Y)` - robot `R` je na pozici `(X,Y)`
* `garbage(R)` - na pozici robota `R` je odpad

Otevřete příklad ve vašem zvoleném editoru - hlavní soubor příkladu se nazývá `mars.mas2j` a popisuje celý multiagentní systém. Základní syntaxe souborů `.mas2j` je poměrně jednoduchá, neměli byste mít problémy ji pochopit - pouze určuje název systému, jakou infrastrukturu používat pro komunikaci (vždy budeme používat `Centralised`, ale Jason podporuje také jade), třídu hlavního prostředí a seznam agentů. Později také uvidíme, že můžeme upřesnit parametry agentů a další věci.

Abyste mohli spustit příklady v jEdit, musíte nastavit cestu k souboru `jason.jar` a k `ant-libs`. Tato nastavení najdete v části `Plugins -> Plugin Options -> Jason`. Nakonec musíte kliknout na zelenou šipku v pravém dolním rohu.

Pro spuštění novějších verzí Jasonu stačí použít příkaz jason `mars.mas2j`. Soubor jason najdete v adresáří `bin` v archivu s instalací Jasonu.

---

## 3. Architektura prostředí

Prostředí, ve kterém agenti operují, je v tomto příkladu reprezentováno mřížkou `7x7`. V simulaci fungují dva roboti: jeden sbírá odpadky, druhý sedí uprostřed a donesené odpadky spaluje.

**Instrukce k analýze kódu:**
Prozkoumejte implementaci prostředí v Javě. Zajímejte se především o třídu `Environment`.

**Otázky k zamyšlení nad kódem:**
* Jakou roli hraje metoda `init(String[])`?
* Jak metoda `executeAction(String ag, Structure action)` zpracovává akce agentů a jejich parametry?
* Kde přesně se volá metoda `updatePercepts()`, která aktualizuje vjemy (percepts) zasílané agentům?

### Implementace prostředí

Z hlediska implementace je prostředí pro agenta vytvořeno jako implementace třídy Environment. Je potřéba napsat metody `init(String []`) a executeAction(String ag, Structure action). Prostředí obvykle používá model, v tomto případě `GridWorldModel`, který lze použít pro prostředí implementovaná jako mřížky. V metodě executeAction vidíte, jak můžeme provádět akce v prostředí a jak zpracovat parametry takových akcí. Metoda také volá metodu updatePercepts, která aktualizuje vjemy agentů.

Jason může spolupracovat i s dalšími frameworky pro implementaci složitějších prostředí, jako je Cartago. **Tyto frameworky ale nebudeme potřebovat a nebudeme se jim ani dále věnovat.**

---

## 4. Architektura Agenta (AgentSpeak)

Každý program agenta v AgentSpeaku sestává z pravidel, počátečních beliefs (přesvědčení), plánů a cílů.

### Implementace agenta
Každý program v AgentSpeaku se skládá z několika částí - pravidel, počátečních beliefs a plánů a cílů.

### 4.1 Beliefs a Pravidla
Pravidla v AgentSpeaku vyjadřují vztah mezi beliefs nebo jiné obecné informace o světě. Chceme-li například napsat pravidlo, které platí, že pokud se robot `r1` nachází na stejném místě jako robot `P` (a umístění robotů je v beliefs reprezentováno jako `pos(R, X, Y)`), můžeme definovat predikát `at(P)` jako

`at(P) :- pos(P,X,Y) & pos(r1,X,Y)`.

Všimněte si, že syntaxe pravidel je podobná syntaxi Prologu. Jediný rozdíl je v tom, že místo čárky jsou atomy odděleny znakem `&`. Interpret AgentSpeaku dokonce při použití pravidla provádí unifikace a odvození je také podobné Prologu.

Jak pravidla, tak další atomy, které dáte agentovi hned na začátku tvoří množinu počátečních beliefs, které se později během samotné simulace mohou měnit. A na základě toho může agent uvažovat a chovat se. Beliefs se automaticky aktualizují vždy, když se agent něco dozví od prostředí, nebo od jiného agenta. Každá belief může mít navíc anotaci (psanou v hranaté závorce za ní), která říká, odkud se agent o této věci dozvěděl, např. `likes(peter, music)[source(martin)]`, říká, že agent věří, že agent petr má rád hudbu, protože mu to řekl agent martin. Anotace se dají unifikovat stejně, jako samotné beliefs.

### 4.2 Plány
Chování agenta je dáno především jeho plány. Popis plánu v Jasonu je opět velmi jednoduchý. Každý plán se skládá ze třech částí – triggeru, kontextu a vlastního těla.

Trigger určuje, kdy se má plán spustit. Z pohledu syntaxe je velmi podobný hlavě predikátu v Prologu s tím, že před samotnou hlavou je pár dalších znaků, které určují, jestli se plán má spustit při přidání (`+`) nebo odebrání (`-`) belief (nic), cíle (`!`, případně `?` pro dotazovací cíle). Pokud se podíváte na vzorového agenta, můžete například najít plán, který se spustí při přidání belief `garbage(r1)`, ten má trigger tvaru `+garbage(r1)`. Také si můžete všimnout triggeru pro přidání počátečního plánu `check(slots)`, který má tvar `+!check(slots)`. U triggerů funguje, stejně jako u hlav pravidel, unifikace v Prologovském významu. Unifikované proměnné jsou potom vázané i v kontextu a těle plánu.

Další část plánu (je od triggeru oddělená dvojtečkou) určuje kontext tohoto plánu. Je to jakási podmínka, která musí platit, aby plán bylo možné spustit. Syntaxe samotné podmínky je stejná, jako syntaxe těla nějakého pravidla. Zase se můžete podívat na vzorového agenta a najdete plán

`+!ensure_pick(S) : garbage(r1)`

`[...]`

Část za dvojtečkou je právě kontext – na místě, kde je agent `r1` musí být nějaký odpad.

Když se na vzorového agenta podíváte pořádně, najdete i kontexty, které obsahují klíčové slovo not. To znamená, že agent o podmínce za ním nevěří, že je pravda. Ještě existuje operátor `~`, který naopak znamená, že agent věří, že podmínka za ním není pravda.

Poslední část každého plánu je jeho samotné tělo. To se od kontextu odděluje pomocí symbolu `<-` a obsahuje posloupnost akcí, které má agent provést. Akce mohou být různých druhů. Akce, které poskytuje prostředí, se píšou jen tak, bez čehokoliv před nimi (např. `pick(garb)`), naproti tomu interní akce agenta (`.print(“Hello world”)`) mají před samotnou akcí tečku.

Speciální druh akcí potom tvoří akce pro přidání plánu (je před nimi `!`, nebo `?` pro dotazovací plány), přidání (`+`) a odebrání (`-`) belief. Jako zkratka, pro update belief slouží operátor (`-+`). Pokud se chcete dotázat svojí belief base použijete operátor `?` (v zásadě jde o přidání dotazovacího cíle).

Speciální typ plánu je dotazovací plán. Ten se může hodit, pokud chceme zjistit nějakou věc, které není přímo v beliefs (např. agent, který je v obýváku a chce vědět, jestli je v ledničce pivo se k ní musí napřed přesunout).

Plány definují samotné chování agenta a skládají se ze tří částí: **trigger**, **kontext** a **tělo**.

**Zadání:** Otevřete kód agenta a identifikujte tyto tři části na konkrétním plánu.

1. **Trigger (Spouštěč):** Určuje, kdy se plán aktivuje (např. při přidání belief `+` nebo cíle `+!`). Příklad: `+garbage(r1)` nebo `+!check(slots)`.
2. **Kontext (Podmínka):** Podmínka oddělená dvojtečkou `:`, která musí platit pro spuštění plánu. Příklad z kódu: `+!ensure_pick(5): garbage(r1)`. Lze zde použít operátor `not` (agent nevěří, že podmínka platí).
3. **Tělo plánu:** Obsahuje posloupnost akcí k provedení, které jsou odděleny symbolem `<`. Rozlišujeme akce prostředí (např. `pick(garb)`) a interní akce uvozené tečkou (např. `.print("Hello world")`).


---

## 5. Praktické implementační úkoly

Aplikujte nabyté znalosti úpravou projektu `cleaning-robots`. Upravujte kódy agentů i konfigurační soubor.

* **Úkol 1:** Přidejte do prostředí dalšího spalovacího robota.
* **Úkol 2:** Aktualizujte kód uklízecího robota tak, aby nosil nalezené odpadky k **nejbližšímu** spalovacímu robotovi.
* **Úkol 3:** Přidejte do MAS další uklízecí roboty a pozorujte výsledné chování systému.

---

## 6. Samostatné studium (volitelné)

**Zadání:** V adresáři `examples` otevřete projekt `blocks-world`. 

**Pozorování:** Cílem tohoto systému je stavění věží z bloků. Zdrojový kód je dobře okomentován. 

---

## Závěrečný blok

### Kontrolní seznam dokončení
- [ ] jEdit je spuštěn a cesty k `jason.jar` a `ant-libs` jsou korektně nastaveny.
- [ ] Původní projekt `cleaning-robots` úspěšně zkompiloval a proběhla vizualizace.
- [ ] Jsou identifikovány a pochopeny metody `init`, `executeAction` a `updatePercepts` v Javě.
- [ ] Jsou úspěšně implementovány úpravy chování robotů (přidání spalovače, optimalizace cesty).

### Závěrečné otázky
1. Jak interpret Jason odlišuje volání akcí poskytovaných prostředím od interních akcí agenta? Jaký syntaktický znak k tomu využívá?
2. K čemu přesně slouží kontext plánu a co se stane, pokud kontext v době aktivace triggeru (např. přidání cíle) není splněn?
3. Jaký je rozdíl mezi dotazovacím plánem (spuštěným znakem `?`) a prostým ověřením přesvědčení v kontextu?

### Krátká sebereflexe
Zamyslete se nad tím, co pro vás bylo při přechodu z procedurálního Pythonu na logický a agentní přístup AgentSpeaku nejobtížnější. 

Zapište si zjištění do vlastních poznámek pro snazší přípravu k zápočtu.