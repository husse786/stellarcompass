<script>
    import LessonPlayer from "$lib/components/LessonPlayer.svelte";
    
    // Runes: Receive props
    let { data } = $props();
    
    // Runes: Derived value (ensure reactivity)
    let lesson = $derived(data.lesson);
</script>

<div class="row justify-content-center">
    <div class="col-lg-10">
        <div class="mb-3">
            <a href="/dashboard" class="btn btn-outline-secondary btn-sm">
                &larr; Zurück zum Dashboard
            </a>
        </div>

        <LessonPlayer 
            title={lesson.title} 
            content={lesson.content}
            videoUrl={lesson.videoUrl}
            contentType={lesson.contentType}
        />

        <div class="mt-4 text-end">
            <button class="btn btn-success btn-lg" disabled>
                Quiz starten (Bald verfügbar)
            </button>
        </div>
    </div>
</div>