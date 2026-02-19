
# Collaborative Ticket Orchestration & Resolution Engine
---
Un sistem complex de gestionare a tichetelor scris in Java (bazat pe principii OOP), conceput pentru coordonarea 
workflow-urilor de dezvoltare software în echipe distribuite. Platforma orchestrează ciclul complet de viață al problemelor:
de la raportarea inițială a bug-urilor și cererilor de funcționalități până la rezolvarea bazată pe milestone-uri și evaluarea 
stabilității aplicației, implementând un sistem de notificare si update in timp real la nivelul structurii organizatorice.
Permite utilizatori cu diverse roluri administrative, care compun o relație ierarhică la nivelul accesului la diverse 
date & metadate ale tichetelor, ca de exemplu:
- **reporter** ->  Raportează tichete (BUG, FEATURE_REQUEST, UI_FEEDBACK) în perioadele de testare, poate comenta pe propriile
tichete și primește notificări despre evoluția acestora. Suportă raportare anonimă exclusiv pentru bug-uri (având prioritate automată LOW)
- **developer** -> Preia și rezolvă tichete conform restricțiilor de senioritate (Junior/Mid/Senior) și domeniu de expertiză 
(Frontend/Backend/DevOps/Design/DB/Fullstack). Accesează doar tichetele din milestone-urile la care este alocat, cu escaladare 
automată când prioritatea depășește nivelul de senioritate.
- **manager** -> Coordonează echipe de developeri, creează și gestionează milestone-uri cu dependențe și deadline-uri, generează
rapoarte complexe de performanță și stabilitate. Are vizibilitate completă asupra tuturor tichetelor din sistem și poate efectua
căutări avansate multi-criteriu.

Dinamica analizei se desfașoară in mai multe faze: faza de _**testare**_, faza de _**soluționare**_ și faza de _
**concluzii & raportare**_ astfel:
1. **Testing (FIXED TIME WINDOW - 12 days)** -> Reporterii raportează probleme identificate pe baza feedback-ului clienților 
și testării interne. Developerii nu pot rezolva tichete, iar managerii nu pot crea milestone-uri în această perioadă.
2. **Solutioning** -> Managerii creează milestone-uri cu alocări de echipă și dependențe între ele. Developerii preiau tichete 
conform expertise-ului, cu escaladare automată de prioritate la fiecare 3 zile și notificări critice cu o zi înainte de deadline
Milestone-urile pot bloca reciproc rezolvarea tichetelor.
3. **Reports** -> Generare de metrici de stabilitate (Customer Impact, Resolution Efficiency, Ticket Risk etc.) normalizate,
care compuse, determină dacă aplicația este STABLE (încheiere proiect), PARTIALLY STABLE sau UNSTABLE (reluare ciclu).

### Characteristics:
- Sistem de notificări event-driven pentru toate acțiunile semnificative (comentarii, schimbări status, alocări, deadline-uri): **Observer DP**
- Mecanisme complexe de blocare între milestone-uri cu deblocare automată la închiderea ultimului tichet blocant
- Calculul automat al scorurilor de performanță diferențiate pe senioritate cu bonusuri progresive
- Căutare avansată cu filtrare multi-criteriu (keywords case-insensitive, date, prioritate, disponibilitate pentru alocare)
- Istoric complet al acțiunilor pe tichete (ASSIGNED, DE-ASSIGNED, STATUS_CHANGED, ADDED_TO_MILESTONE, REMOVED_FROM_DEV etc.)
---

Clasa `App` reprezintă punctul de intrare al aplicației și orchestrează fluxul principal de execuție: deserializare JSON
complexa, foloseste Jackson ObjectMapper cu anotări avansate (`@JsonSubTypes`, `@JsonProperty`, `@JsonCreator` din Jackson,
in pom.xml (Maven)) pentru maparea automată a utilizatorilor din structura JSON ierarhică (`input/database/users.json`).
Procesarea se face in functie de comenzile primite (si ele tot serializate JSON, pentru testare): fiecare comandă din input 
este deserializată într-un obiect `Command` care își gestionează propria logică de execuție, logică de citire, procesare și 
scriere izolată, facilitând testarea și extinderea eventualelor comenzi.

`ErrLogger` implementează **Singleton** (garanteaza existenta unei singure instante pe parcursul executatiei unui workflow) 
cu lazy initialization și functioneaza ca wrapper centralizat pentru funcționalități ortogonale:
- logFileWrite: captează excepții și evenimente de debug într-un fișier persistent (`err_log2.txt`)
- state management global: mentine starea aplicației (utilizatori, milestone-uri, timestamp curent, metadate curente)
- evidenta temporala: integreaza`TimeManager` pentru sincronizarea automată a evenimentelor bazate pe timp
- constante: definește magic numbers (INT10, INT12, DBL0_5, logFilePath etc.) pentru consistenta în statistici si logging

Daca codul va deveni vreodata organizat multi-threaded, clasa poate fi usor reorganizata in forma cu Enum (garantat thread-safe
de JVM), @Bloch's form.

Engine-ul combina **Factory Pattern** cu **Builder Pattern** pentru a gestiona instanțierea tichetelor cu atribute obligatorii 
și optionale diferențiate pe tipuri. Fiecare tip de tichet are atribute comune, atribute specifice obligatorii si atribute specifice optionale.
- **Atribute comune**: id, title, businessPriority, status, expertiseArea, description, reportedBy
- **Atribute specifice obligatorii**:
    - BUG: expectedBehavior, actualBehavior, frequency, severity
    - FEATURE_REQUEST: businessValue, customerDemand
    - UI_FEEDBACK: businessValue, usabilityScore
- **Atribute opționale variabile**: environment, errorCode, screenshotUrl etc. Astfel, cu Builder DP asiguram baza obiectelor
(Obiect immutabil după construcție), iar cu Factory abstractizam tipul concret. Clasa `Command` nu știe că lucrează cu `Bug` / 
`UI_Feedback` / `Feature_Request`, doar cu `Ticket` abstract, iar adaugarea unui nou tip de tichet e echivalenta cu o nouă factory, 
fără modificări în `Command`, sustinand principiul _Open-Close_.
---
Clasa `User` folosește Builder DP cu **clasă internă statica Builder**  cu atribute obligatorii în constructor (username, 
email, role) și metode fluent pentru câmpuri role-specific: Developerii au expertiseArea și seniority pentru validarea 
compatibilității cu tichetele, Managerii au subordinates[] pentru orchestrarea echipelor, iar Reporterii au doar câmpurile
de bază. Constructor-ul privat `User(Builder builder)` garanteaza ca instanțele sunt create exclusiv prin builder, prevenind
stări inconsistente.

Sistemul implementeaza un sistem de notificări event-driven (motive temporale, lucru cu  TimeManager) unde `Milestone` (Subject) 
comunca automat cu `User` (Observer) fără dependente directe între ele. Când un eveniment semnificativ apare (deadline in ziua 
urmatoare, milestone deblocat, tichete care devin CRITICAL), `Milestone` apelează `notifyObservers(String message)` care 
iterează prin lista de observatori înregistrați și trimite un trigger `user.update(message)` pentru fiecare developer alocat 
milestone-ului. Această abordare respectă **Dependency Inversion**: ambele clase depind de interfata `Observer`, nu una de cealalta.

Strategy DP este folosit pentru metrici si permite calculul dinamic al scorurilor de tipul Customer Impact, Ticket Risk, 
Resolution Efficiency fără a modifica clasa `MetricsManager`. Interfața `MetricStrategy` definește contractul comun cu metode 
default pentru agregări standard `TotalNumber`, `TotalTicketsType`, `TotalTicketsPriority` și metoda abstractă `TotalTicketsParticular(List<Ticket>)`
care implementează formula specifică fiecărei metrici. Astfel, `MetricsManager` operează pe interfata `MetricStrategy` făra să mai 
stie detaliile de calcul, respectând **Open-Closed Principle**. Acesta este folosit si pentru calculul performantelor developerilor 
in functie de senioritate.

Ultimul Design Pattern din arhitectura engine-ului este **Specification**, folosit pentru construirea criteriilor de căutare
din componente modulare (reutilizabile) in cazul comenzii de cautare cu filtre. Pentru ca exista doua tipuri de cautari: 
TICKET / DEV, interfața `Specification<T>` este generică si dispune de metode helper default (operatii logice in vederea construirea 
query-urilor complexe prin composability: OR, AND, NOT) si  `boolean isSatisfiedBy(T item)`. Pentru verificarea filtrului de 
`Keywords` folosim operatorul OR pentru a verifica existenta macar a unui keyword, peste care se aplica operatorul AND ca la celelalte.
