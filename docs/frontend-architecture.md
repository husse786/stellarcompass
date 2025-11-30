# Frontend Architektur (SvelteKit)

## 1. Routing Struktur (src/routes)

### Öffentlich

* `/` - Landing Page (Projektvorstellung)
* `/login` - Login Seite

### Geschützter Bereich (Gruppiert unter (app))

*Alle Routen hier erfordern ein valides Token.*

### Schülerin (Student)

* `/dashboard` - Übersicht Level & Fortschritt
* `/learn/[levelId]/[subjectId]` - Fächerübersicht
* `/learn/.../lesson/[lessonId]` - Lektion ansehen (Video/Text)
* `/learn/.../lesson/[lessonId]/quiz` - KI-Quiz durchführen

### Mentorin (Mentor)

* `/mentor/dashboard` - Liste aller zugewiesenen Schülerinnen
* `/mentor/student/[studentId]` - Detailansicht Lernfortschritt

### Administrator (Admin)

* `/admin/users` - Benutzerverwaltung
* `/admin/content` - Lehrplan-Editor

## 2. Kern-Komponenten (src/lib/components)

* **`QuizInterface.svelte`**: Interaktives Quiz mit KI-Feedback-Loop.
* **`LessonPlayer.svelte`**: Rendert Markdown-Content oder YouTube-Videos.
* **`RoleGuard.svelte`**: Schützt Inhalte basierend auf der User-Rolle.
* **`ProgressBar.svelte`**: Visualisiert den Lernfortschritt.
