# Qualitäts-Checkliste: Requirements Engineering

*Diese Kriterien müssen erfüllt sein, bevor wir "Ready for Dev" sagen.*

## 1. Use Case Diagramm (UML)

- [ ] **Systemgrenze:** Ist klar erkennbar, was *innerhalb* und was *ausserhalb* der App liegt?
- [ ] **Akteure:** Sind alle menschlichen (Schülerin, Mentor) und technischen Akteure (KI-Service, Auth0) benannt?
- [ ] **Vollständigkeit:** Decken die Use Cases die gesamte Vision ab (Lernen, Mentoring, Administration)?
- [ ] **Beziehungen:** Sind `include` und `extend` Beziehungen korrekt verwendet (falls nötig)?

## 2. Use Case Beschreibungen (Text)

- [ ] **Format:** Wird eine Standard-Vorlage genutzt (Name, Akteur, Vorbedingung, Ablauf, Ergebnis)?
- [ ] **Granularität:** Beschreibt der Use Case einen fachlichen Prozess (z.B. "Kurs belegen") und nicht nur einen Klick (z.B. "Button drücken")?
- [ ] **Exceptions:** Sind Fehlerfälle beschrieben (z.B. "Keine Internetverbindung", "Falsche Eingabe")?
- [ ] **Testbarkeit:** Kann ein Tester anhand der Beschreibung später prüfen, ob die Funktion korrekt ist?

## 3. Fachliches Datenmodell (ERD)

- [ ] **Entitäten:** Sind alle Objekte aus der Realität abgebildet (Nutzer, Kurs, Lektion, Prüfung)?
- [ ] **Attribute:** Haben die Entitäten die wichtigsten Eigenschaften (z.B. `User.email`, `Lesson.content`)?
- [ ] **Beziehungen:** Stimmen die Kardinalitäten (1:1, 1:n, m:n)? (Beispiel: Kann eine Lektion zu mehreren Kursen gehören?)
- [ ] **Zustände:** Ist definiert, welche Status-Werte kritische Objekte haben (z.B. `QuizStatus`: DRAFT, PUBLISHED, ARCHIVED)?

## 4. UI Mockups / Skizzen

- [ ] **Vollständigkeit:** Gibt es einen Screen für jeden Haupt-Use-Case?
- [ ] **Navigation:** Ist erkennbar, wie man durch die App navigiert (Menü, Zurück-Buttons)?
- [ ] **Rollen-Sicht:** Gibt es unterschiedliche Ansichten für Schülerin vs. Mentorin?
- [ ] **Realismus:** Sind die gezeigten Datenfelder konsistent mit dem ER-Modell?
