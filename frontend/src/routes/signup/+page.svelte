<script>
    // Runes: Props empfangen (für Fehlermeldungen vom Server)
    let { form } = $props();
</script>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow">
                <div class="card-header bg-success text-white">Registrierung</div>
                <div class="card-body">
                    
                    {#if form?.error}
                        <div class="alert alert-danger" role="alert">
                            {form.error}
                        </div>
                    {/if}

                    <form method="POST" action="?/signup">
                        <div class="row mb-3">
                            <div class="col">
                                <label for="firstName" class="form-label">Vorname</label>
                                <input type="text" class="form-control" id="firstName" name="firstName" placeholder="Maria">
                            </div>
                            <div class="col">
                                <label for="lastName" class="form-label">Nachname</label>
                                <input type="text" class="form-control" id="lastName" name="lastName" placeholder="Muster">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label">E-Mail *</label>
                            <input 
                                type="email" 
                                class="form-control" 
                                id="email" 
                                name="email" 
                                required 
                                value={form?.email ?? ''}
                            >
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label">Passwort *</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                            <div class="form-text">Mindestens 8 Zeichen, Gross-/Kleinschreibung & Zahlen.</div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success">Konto erstellen</button>
                        </div>

                        <div class="mt-3 text-center">
                            <small>Bereits ein Konto? <a href="/login">Einloggen</a></small>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>