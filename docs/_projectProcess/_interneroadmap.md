# Interne Projekt-Roadmap: Stellar Compass

**Strategie:** Agile Development & "Fail Fast"
**Ziel:** Kontinuierliche Abgabe-Bereitschaft (Deploy early, Test often)

---

## 📅 PHASE 1: Fundament & "Walking Skeleton" (Woche 1 - 3)

*Ziel: Die Verwaltung ist erledigt, die Architektur steht und eine leere App läuft bereits online auf Azure.*

### Woche 1: Setup & Design

- [x] **GitHub:** Repo erstellen (Private), Project Board (Scrum) einrichten.
- [x] **Modeling (Prio 1):** ER-Diagramm (3 Entitäten) & Use-Case Diagramm erstellen.
- [x] **Termin:** Vorstellung der Idee mit ER-Diagram und Use-Case Diagram

### Woche 2: Boards & Code Init

- [x] [cite_start]**Abgabe:** Explore/Create/Evaluate Boards auf Moodle hochladen[cite: 76].
- [ ] **Backend:** Spring Boot Projekt initialisieren (Web, Data-MongoDB, Security Dependencies).
- [ ] **Frontend:** Svelte Projekt initialisieren.
- [ ] **Pipeline V1:** GitHub Action erstellen, die bei Push baut (`mvn clean verify`).

### Woche 3: Deployment & Review

- [ ] [cite_start]**Peer-Review:** Boards der anderen bewerten[cite: 77].
- [ ] **Cloud:** Dockerfile erstellen. App auf Azure App Service deployen.
  - [cite_start]*Meilenstein:* "Hello World" ist unter einer öffentlichen URL erreichbar[cite: 90].

---

## 📅 PHASE 2: Der Pitch & Das Daten-Rückgrat (Woche 4 - 6)

*Ziel: Der Pitch ist bestanden. Im Hintergrund läuft die Datenbank und der Login.*

### Woche 4: Pitch Fokus

- [ ] [cite_start]**Pitch Deck:** Finalisieren und abgeben[cite: 78].
- [ ] **Probelauf:** Pitch 3-4 mal üben (Timing!).
- [ ] **Tech:** MongoDB Atlas Cluster erstellen und Connection-String im Backend hinterlegen (Secrets in GitHub!).

### Woche 5: Der Pitch & Security

- [ ] [cite_start]**EVENT:** Pitch vor der Klasse halten[cite: 79].
- [ ] **Security:** Auth0 Account erstellen.
- [ ] **Code:** Spring Security so konfigurieren, dass Endpoints geschützt sind. Login im Svelte Frontend einbauen.

### Woche 6: Datenstruktur

- [ ] **Backend:** Entities in Java erstellen (User, Course, Quiz).
- [ ] **DB:** Repositories anlegen. Testen, ob Daten in MongoDB gespeichert werden.
- [ ] **Testing:** Erste JUnit Tests für die Repositories schreiben.

---

## 📅 PHASE 3: Core Features & Frontend (Woche 7 - 9)

*Ziel: Die App kann benutzt werden. Ein User kann einen Kurs starten und beenden.*

### Woche 7: Business Logik

- [ ] **Feature:** "Kurs belegen" implementieren.
- [ ] [cite_start]**Zustandsmaschine:** Logik bauen für Statuswechsel (z.B. `ENROLLED` -> `IN_PROGRESS` -> `COMPLETED`)[cite: 96].
- [ ] **Tests:** Unit-Tests für die Service-Layer schreiben (Logik absichern).

### Woche 8: UI Bauen (Svelte)

- [ ] **Frontend:** Kurs-Übersicht und Detail-Seite bauen.
- [ ] **API:** Frontend an das Spring Boot Backend anbinden (Fetch Data).
- [ ] [cite_start]**Ownership:** Sicherstellen, dass User nur *ihre* Daten sehen[cite: 96].

### Woche 9: Integration

- [ ] **End-to-End:** Funktioniert der Flow? Login -> Kurs wählen -> Status ändern.
- [ ] **Coverage Check:** JaCoCo Report prüfen. [cite_start]Sind wir auf Kurs Richtung 90%?[cite: 101]. Wenn nein: Tests nachschreiben!

---

## 📅 PHASE 4: The "Wow" Factor & KI (Woche 10 - 11)

*Ziel: Die KI-Funktion (das Alleinstellungsmerkmal) wird eingebaut.*

### Woche 10: Spring AI

- [ ] **Integration:** Spring AI Dependency hinzufügen.
- [ ] **API:** Verbindung zu OpenAI (oder Azure OpenAI) herstellen.
- [ ] **Feature:** "Quiz Generator" – Prompt Engineering (z.B. "Erstelle 3 Fragen zu diesem Text").

### Woche 11: Qualitätsoffensive

- [ ] [cite_start]**Testing:** Controller-Tests mit `@WebMvcTest` und Mocking vervollständigen[cite: 92].
- [ ] **CI/CD:** Prüfen, ob Tests in der GitHub Pipeline grün sind und Coverage-Report generiert wird.
- [ ] [cite_start]**SonarQube:** (Optional) Code-Analyse laufen lassen und "Code Smells" fixen[cite: 113].

---

## 📅 PHASE 5: Dokumentation & Finish (Woche 12 - 14)

*Ziel: Aufräumen, Dokumentieren und Abgeben.*

### Woche 12: Doku First

- [ ] [cite_start]**README.md:** Alle Kapitel gemäss Moodle-Vorlage ausfüllen[cite: 125].
- [ ] **Visuals:** Screenshots der fertigen App machen und in `/doc` speichern.
- [ ] [cite_start]**API Doku:** Postman Collection exportieren und veröffentlichen[cite: 103].

### Woche 13: Final Polish

- [ ] **Clean Up:** Code aufräumen, Kommentare entfernen.
- [ ] **Checkliste:** Die "Master-Checkliste" Punkt für Punkt durchgehen.
- [ ] [cite_start]**Collaborators:** Sicherstellen, dass alle Dozenten Zugriff haben[cite: 160].

### Woche 14: ABGABE (Deadline: 21.12.2025)

- [ ] [cite_start]**Zips erstellen:** Repo-Download, JaCoCo-Report, Surefire-Reports [cite: 163-166].
- [ ] **Credentials:** Liste mit URLs und Test-Logins erstellen.
- [ ] **Upload:** Alles auf Moodle hochladen.
- [ ] **Feiern.** 🎉

---

## 📅 Ausblick: Präsentation (KW 2 2026)

- [ ] [cite_start]**Januar:** Demo vorbereiten [10 Min](cite: 155).
