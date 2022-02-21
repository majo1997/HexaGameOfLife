Autor projektu: Mário Hlavačka
Názov projektu: Hexa Game of Life

Projekt slúži ako simulácia Game of Life v šesťuholníkovej mriežke. Po spustení programu je simulácia zastavená. Keď je simulácia zastavená
tak môžeme meniť stavy buniek v mriežke(aktivovať či deaktivovať bunku). Následne môžeme simuláciu spustiť. Pre každú bunku platí že prežije len
ak má práve 2 aktívnych susedov(počet aktívnych susedov je v programe zadefinované ako konštanta). Môžeme meniť aj veľkosť mriežky zväčšovaním šírky
alebo výšky okna programu. Ak však zmenšíme okno tak strácame hodnoty buniek, čiže ak ho znova potom rozšírime tak pribudnú neaktívne bunky. V programe
sa dá vracať aj o generácie naspäť ale maximálne o N generácii(tiež v programe zadefinované ako konštanta a dá sa meniť). Simulácia sa dá aj uložiť a
načítať zo súboru aj s ich predchádzajúcimi generáciami, čiže po načítaní sa dá tiež vracať o pár generácii naspäť. Dá sa ešte vyčistiť mriežka aj
zmeniť veľkosť šesťuholníkov pomocou slidera. Bunky ktoré sú aktívne po viacero generácii tak im farba bledne ale len do určitého limitu(tiež je v
programe ako konštanta napr. 0.7, pričom 0 je čierna a 1 je biela). Javadoc dokumentácia je vygenerovaná v priečinku documentation.

Na spustenie projektu, projekt stačí len extrahovať a po extrahovaní sa spúšťa cez triedu HexaGameOfLife. Samozrejme treba pridať javafx k projektu
a doplniť do VM options --module-path "cesta_ku_javafx\javafx-sdk-11.0.2\lib" --add-modules=javafx.controls,javafx.fxml