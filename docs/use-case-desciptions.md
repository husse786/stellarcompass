# Stellar Compass – Use Case Dokumentation

Version: 1.0  
Status: Draft  
Erstellt: 2025-XX-XX  
System: Stellar Compass (Webbasierte Lernplattform)

---

## 1. Systemübersicht

Stellar Compass ist eine webbasierte digitale Lernplattform für Schülerinnen, Mentoren und Administratoren.  
Jeder Schüler durchläuft Klassen, Fächer und Lektionen.  
Nach jeder Lektion erstellt die KI automatisch ein Quiz, inklusive Erklärungen, Wiederholungsfragen und Schwierigkeitsanpassung.

Das System umfasst folgende Kernbereiche:

- **Lernbereich (Student Flow)**  
- **Mentorbereich (Monitoring & Content Editing)**  
- **Adminbereich (User & Content Management)**  
- **KI-Dienst (Quizgeneration & Adaptive Explanations)**

---

## 2. Akteure

### 2.1 Schüler (Primary Actor)

- startet Lektionen
- führt Quiz durch
- erhält KI-Erklärungen
- sieht Fortschritt
- kontaktiert Mentor

## 2.2 Mentor

- sieht zugewiesene Schüler
- prüft Fortschritt
- bearbeitet Inhalte (Fächer, Lektionen)
- sendet Nachrichten an Schüler
- verwaltet Expertise-Tags

## 2.3 Administrator

- verwaltet Benutzer & Rollen
- sperrt/entsperrt Benutzer
- verwaltet Inhalte systemweit
- weist Mentoren Schülern zu
- übernimmt Moderation & technische Settings

## 2.4 KI-System (externe Systemkomponente)

- generiert Quizfragen
- erstellt Erklärungen
- passt Schwierigkeit an
- generiert Wiederholungsfragen

---

## 3. Use-Case-Liste

### Schüler-Use Cases

- UC-S1: Registrierung durchführen  
- UC-S2: Login durchführen  
- UC-S3: Dashboard anzeigen  
- UC-S4: Klasse öffnen  
- UC-S5: Fach öffnen  
- UC-S6: Lektion starten  
- UC-S7: Quiz starten  
- UC-S8: Quiz beantworten  
- UC-S9: KI-Erklärungen abrufen  
- UC-S10: Quiz wiederholen  
- UC-S11: Fortschritt einsehen  
- UC-S12: Mentor kontaktieren  

### Mentor-Use Cases

- UC-M1: Mentor-Login  
- UC-M2: Schülerliste anzeigen  
- UC-M3: Schülerfortschritt prüfen  
- UC-M4: Schüler kontaktieren  
- UC-M5: Inhalte bearbeiten  
- UC-M6: Quiz-Inhalte prüfen/bearbeiten  
- UC-M7: Mentorprofil verwalten  

### Admin-Use Cases

- UC-A1: Benutzer verwalten  
- UC-A2: Benutzer sperren/löschen  
- UC-A3: Inhalte verwalten  
- UC-A4: Monitoring durchführen  
- UC-A5: Mentor-Zuweisung durchführen  
- UC-A6: Systemeinstellungen verwalten  

### KI-Use Cases

- UC-K1: Quiz generieren  
- UC-K2: Fehleranalyse erzeugen  
- UC-K3: Erklärung vereinfachen  
- UC-K4: Erklärung mit Beispiel erstellen  
- UC-K5: Wiederholungsfragen erzeugen  

---

## 4. Detail-Use-Cases

### UC-S7: Quiz starten

**Akteur:** Schüler  
**Ziel:** Ein Quiz zur gerade abgeschlossenen Lektion starten.  

#### Preconditions

- Schüler ist eingeloggt
- Schüler hat eine Lektion abgeschlossen
- KI-Service ist verfügbar

#### Postconditions

- Quizfragen sind generiert und angezeigt
- Quiz ist im Status „offen“

#### Basic Flow

1. Schüler klickt auf „Quiz starten“.
2. System sendet die Lektion an den KI-Dienst.
3. KI generiert ein Quiz basierend auf dem Inhalt.
4. System zeigt das Quiz dem Schüler an.
5. Schüler kann nun Antworten eingeben.

#### Alternate Flows

A1 – KI nicht verfügbar

- System zeigt Fehlermeldung
- Schüler erhält Option, später erneut zu starten

---

### UC-S8: Quiz beantworten

**Akteur:** Schüler
**Ziel:** Die Quizfragen beantworten und auswerten lassen.

#### Preconditions (UC-S8)

- Quiz ist gestartet

#### Postconditions (UC-S8)

- Ergebnis ist gespeichert
- Fehlerhafte Fragen sind markiert
- Erklärungen stehen bereit

#### Basic Flow (UC-S8)

1. Schüler wählt für jede Frage eine Antwort.
2. Schüler klickt auf „Abschliessen“.
3. System bewertet die Antworten.
4. System zeigt Ergebnis (Punkte, Prozente).
5. Fehlerhafte Antworten werden hervorgehoben.

#### Alternate Flows (UC-S8)

A1 – Unterbrechung / Browser geschlossen

- Quizstatus wird gespeichert
- Schüler kann später fortsetzen

---

### UC-S9: KI-Erklärung abrufen

**Akteur:** Schüler
**Ziel:** Bei einer falschen Antwort eine adaptive KI-Erklärung erhalten.

#### Preconditions (UC-S9)

- Mindestens eine Frage wurde falsch beantwortet

#### Postconditions (UC-S9)

- Erklärung ist angezeigt
- Schüler kann Schwierigkeitsgrad anpassen

#### Basic Flow (UC-S9)

1. Das System sendet die falschen Antworten an die KI.
2. KI erstellt eine Erklärung basierend auf dem Fehler.
3. Schüler sieht zwei Buttons:
   - „Einfacher machen“
   - „Beispiel anzeigen“
4. Je nach Wahl liefert KI eine alternative Erklärung.

---

### UC-S11: Fortschritt einsehen

**Akteur:** Schüler
**Ziel:** Lernen, welche Klassen, Fächer und Lektionen freigeschaltet sind.

#### Preconditions (UC-S11)

- Schüler ist eingeloggt

#### Postconditions (UC-S11)

- Fortschrittsbalken sind sichtbar
- Lock/Unlock-Status wird korrekt angezeigt

#### Basic Flow (UC-S11)

1. Schüler öffnet das Dashboard.
2. System lädt Fortschrittsdaten:
   - Klassenfortschritt (z. B. 75 %)
   - Fachfortschritt
   - Lektionenstatus
3. System zeigt, ob Klasse 2 gesperrt oder freigeschaltet ist.

---

### UC-M3: Schülerfortschritt prüfen

**Akteur:** Mentor

#### Basic Flow (UC-M3)

1. Mentor öffnet Dashboard.
2. System zeigt Liste der zugewiesenen Schüler.
3. Mentor klickt auf einen Schüler.
4. System zeigt Fortschritt grafisch und textuell.

---

### UC-A1: Benutzer verwalten

**Akteur:** Administrator

#### Basic Flow (UC-A1)

1. Admin öffnet Benutzerliste.
2. Admin sucht einen Benutzer.
3. Admin bearbeitet Daten: Name, Rolle, Status, etc.
4. System speichert Änderungen.

---

## 5. Systemregeln (Business Rules)

**BR-1:** Klasse 2 wird erst freigeschaltet, wenn Klasse 1 ≥ 80 % erreicht ist.
**BR-2:** KI-Quiz basiert immer auf der aktiven Lektion.
**BR-3:** Falsche Antworten müssen Erklärungen enthalten.
**BR-4:** Mentoren sehen nur ihre eigenen Schüler.
**BR-5:** Admin hat immer alle Rechte.

---

## 6. Nicht-funktionale Anforderungen

- **Performance:** Quizgeneration ≤ 10 Sekunden
- **Verfügbarkeit:** 90 %
- **Security:** Rollenbasiertes Zugriffssystem
- **DSGVO-konform**
- **Audit-Logging für Admin-Aktionen**

---

## 7. Open Questions (für spätere Architektur)

- Wie werden KI-Prompts strukturiert?
- Versionierung von Lektionen?
- Mentoren-Zuweisung manuell oder automatisch?
- Wird das Quiz gespeichert oder on-the-fly regeneriert?
