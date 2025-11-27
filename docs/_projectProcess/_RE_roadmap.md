# Phase 0: Requirements Engineering (Der Bauplan)

**Ziel:** Vollständige, schriftliche Spezifikation von "Stellar Compass" *vor* dem Projektstart.
**Output:** 4 zentrale Artefakte, die als Basis für Scrum-Issues dienen.

---

## 📍 Schritt 1: Der Scope (Use Case Diagramm)

*Wir definieren, WER was tun darf. Das zieht die Grenzen des Systems.*

* **Aktion:** Identifikation aller Akteure (z.B. Schülerin, Mentorin, Admin, KI-System).
* **Aktion:** Identifikation der Kern-Anwendungsfälle (z.B. "Lektion absolvieren", "Quiz generieren", "Fortschritt prüfen").
* **Artefakt:** `Use-Case-Diagramm.png` (UML Standard).

## 📍 Schritt 2: Die Details (Use Case Beschreibungen)

*Wir gehen in die Tiefe. Ein Kreis im Diagramm wird zu einer präzisen Anleitung.*

* **Aktion:** Auswahl der wichtigsten Use Cases (nicht triviale Dinge wie "Logout", sondern Kernprozesse).
* **Aktion:** Schreiben der Beschreibungen nach RE-Schema:
  * *Vorbedingung* (Was muss gegeben sein?)
  * *Standardablauf* (Happy Path)
  * *Alternativabläufe* (Was, wenn die KI offline ist? Was, wenn das Quiz fehlschlägt?)
  * *Nachbedingung* (Was ist in der DB passiert?)
* **Artefakt:** `Use-Case-Descriptions.md`

## 📍 Schritt 3: Die Daten (Fachliches Datenmodell / ERM)

*Wir strukturieren das Wissen. Das ist die Basis für MongoDB.*

* **Aktion:** Definition der Entitäten (z.B. `User`, `Course`, `Module`, `Lesson`, `QuizResult`).
* **Aktion:** Definition der Beziehungen und Kardinalitäten (z.B. "Eine Schülerin hat *viele* Quiz-Resultate", "Ein Kurs hat *viele* Module").
* **Aktion:** Zustands-Definition (State Machine): Welche Zustände hat eine `Submission`? (Offen -> Eingereicht -> Korrigiert).
* **Artefakt:** `ER-Modell.png` & Zustands-Diagramm.

## 📍 Schritt 4: Das Gesicht (UI Mockups)

*Wir visualisieren die Lösung. Das ist die Basis für Svelte.*

* **Aktion:** Skizzieren der wichtigsten Screens (Schüler-Dashboard, Lektions-Ansicht, Mentor-Übersicht).
* **Aktion:** Prüfung des Flows: Kommt der User logisch von A nach B?
* **Artefakt:** UI-Mockups (als Bilder/PDF).

---

## 🏁 Abschluss Phase 0

Erst wenn diese 4 Schritte erledigt sind, werden daraus die **Epics und User Stories** für das Scrum Board abgeleitet.
