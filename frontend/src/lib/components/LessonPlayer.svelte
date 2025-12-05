<script>
    // Props empfangen
    let { title, content, videoUrl, contentType } = $props();

    // Helper function to convert YouTube URLs to embed format
    function getEmbedUrl(url) {
        if (!url) return '';
        return url.replace("watch?v=", "embed/");
    }
</script>

<div class="card shadow-lg">
    <div class="card-header bg-white">
        <h2 class="h4 mb-0">{title}</h2>
    </div>
    
    <div class="card-body">
        {#if contentType === 'VIDEO' && videoUrl}
            <div class="ratio ratio-16x9 mb-4">
                <iframe 
                    src={getEmbedUrl(videoUrl)} 
                    title={title} 
                    allowfullscreen
                ></iframe>
            </div>
            <div class="alert alert-info">
                <strong>Videobeschreibung:</strong>
                <p class="mb-0 mt-2">{content}</p>
            </div>

        {:else}
            <div class="typography">
                <p class="lead" style="white-space: pre-wrap;">{content}</p>
            </div>
        {/if}
    </div>
</div>