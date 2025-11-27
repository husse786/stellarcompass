# Projekt-Checkliste Anforderung: Stellar Compass

**Ziel:** Note 6.0
**Deadline Code:** 21.12.2025 (Woche 14)
**Tech Stack:** Spring Boot, Svelte, MongoDB, Auth0, Azure, OpenAI

---

## 1. Projektmanagement & Organisation (Wichtig für Bewertung!)

### GitHub Repository Setup

- [ ] Repo ist auf "private" gestellt.
- [ ] Dozenten als Collaborators hinzugefügt (DavidZhaw, mosazhaw, mmeisterhans, bkuehnis).
- [ ] **Scrum Board** (Projects) erstellt mit Spalten: `Ready`, `In Progress`, `Done`, `Closed`.
- [ ] Sprints (Iterations) sind eingerichtet.
- [ ] **Issues** gepflegt: Jedes Issue hat Beschreibung & Akzeptanzkriterien.
- [ ] **Labels** genutzt: Mind. 3 Stück (z.B. `feature`, `bug`, `chore`).

### Meilensteine & Abgaben

- [ ] **Woche 1-3:** Dozenten-Gespräch (ER-Diagramm & Use-Case Diagramm mitbringen!).
- [ ] **Woche 2:** Upload Boards (Explore/Create/Evaluate) auf Moodle.
- [ ] **Woche 4:** Upload Pitch-Deck.
- [ ] **Woche 5/6:** Pitch halten (benotet).
- [ ] **21.12.25:** Finale Abgabe (Code & Doku).

### Dokumentation (README.md)

*Muss auf Moodle-Vorlage basieren, Bilder im Ordner `/doc`*

- [ ] Einleitung & Board-Zusammenfassung.
- [ ] Feedback-Diskussion aus dem Pitch.
- [ ] **Requirements:** Use-Case Diagramm, ER-Modell, UI-Mockups.
- [ ] **Implementation:** Screenshots der finalen App, KI-Funktionen erklärt.
- [ ] API-Dokumentation (Link zu veröffentlichtem Postman Collection).

---

## 2. Technische Umsetzung (Development)

### Phase 1: Setup & Architektur (Woche 1-3)

- [ ] Spring Boot Projekt aufsetzen (Java 21+, Maven).
- [ ] Ordnerstruktur: `Models`, `Controller`, `Repository`, `Services`.
- [ ] Svelte Frontend aufsetzen.
- [ ] **CI/CD:** GitHub Action für Build & Test bei jedem Push.
- [ ] **Deployment:** "Hello World" Container läuft auf Azure App Service.

### Phase 2: Core Features & Daten (Woche 4-6)

- [ ] **Datenbank:** MongoDB Atlas verbunden.
- [ ] **Security:** Auth0 Login implementiert (Frontend & Backend).
- [ ] **Rollen:** Mind. 2 Rollen eingerichtet (z.B. `Student`, `Mentor`).
- [ ] **Entitäten:** 3 Collections erstellt (z.B. User, Course, Quiz).
  - [ ] Ownership: Eine Entität gehört fest einem User.

### Phase 3: Logik & KI (Woche 7-11)

- [ ] **Zustandsmaschine:** Eine Entität hat Status-Wechsel (z.B. Quiz: Started -> Submitted -> Graded).
- [ ] **KI-Integration:** Spring AI nutzt OpenAI API (z.B. Generierung von Quizfragen aus Kurstext).

### Phase 4: Testing & Quality (Laufend!)

- [ ] **Unit Tests:** JUnit 5 mit Mockito.
- [ ] **Integration Tests:** `@SpringBootTest` & `@MockMvc` für Controller.
- [ ] **Automatisierung:** Tests laufen in GitHub Action.
- [ ] **Coverage:** JaCoCo eingebunden -> **Ziel: 90% Abdeckung**.
- [ ] SonarQube Analyse (Optional für Bonus).

---

## 3. Finale Abgabe-Checkliste (21.12.2025)

Upload auf Moodle

- [ ] Zip des GitHub-Repos.
- [ ] `jacoco.zip` (Test-Reports).
- [ ] `surefire-reports.zip` (Test-Logs).
- [ ] Links: Repo-URL, Azure-URL, Postman-URL.
- [ ] Login-Daten für Demo-User (2 Rollen).
