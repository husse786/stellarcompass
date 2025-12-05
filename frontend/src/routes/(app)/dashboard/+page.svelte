<script>
    let { data } = $props();
    // svelte-ignore state_referenced_locally
        let { subjects, lessons, error } = data;
</script>

<h1 class="mb-4">Dein Lern-Dashboard</h1>

{#if error}
    <div class="alert alert-danger">{error}</div>
{/if}

<div class="row">
    <div class="col-md-4">
        <h3 class="mb-3">Fächer</h3>
        {#if subjects.length === 0}
            <p class="text-muted">Keine Fächer gefunden.</p>
        {:else}
            <div class="list-group">
                {#each subjects as subject}
                    <div class="list-group-item">
                        <h5 class="mb-1">{subject.title}</h5>
                        <p class="mb-1 small">{subject.description}</p>
                    </div>
                {/each}
            </div>
        {/if}
    </div>

    <div class="col-md-8">
        <h3 class="mb-3">Verfügbare Lektionen</h3>
        {#if lessons.length === 0}
            <p class="text-muted">Keine Lektionen verfügbar.</p>
        {:else}
            <div class="row g-3">
                {#each lessons as lesson}
                    <div class="col-md-6">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body">
                                <h5 class="card-title">{lesson.title}</h5>
                                <h6 class="card-subtitle mb-2 text-muted">
                                    {subjects.find(s => s.id === lesson.subjectId)?.title || 'Unbekanntes Fach'}
                                </h6>
                                <p class="card-text text-truncate">
                                    {lesson.content}
                                </p>
                                <a href="/learn/{lesson.subjectId}/lesson/{lesson.id}" class="btn btn-primary btn-sm">
                                    Lektion starten
                                </a>
                            </div>
                        </div>
                    </div>
                {/each}
            </div>
        {/if}
    </div>
</div>