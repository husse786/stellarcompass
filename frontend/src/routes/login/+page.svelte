<script>
    // Runes: Props receive (for error messages from server)
    let { form } = $props();
</script>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">Login</div>
                <div class="card-body">
                    {#if form?.error}
                        <div class="alert alert-danger" role="alert">
                            {form.error}
                        </div>
                    {/if}

                    <form method="POST">
                        <div class="mb-3">
                            <label for="email" class="form-label">E-Mail</label>
                            <input
                                type="email"
                                class="form-control"
                                id="email"
                                name="email"
                                required
                                placeholder="name@example.com"
                            />
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input
                                type="password"
                                class="form-control"
                                id="password"
                                name="password"
                                required
                            />
                        </div>
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Einloggen</button>
                        </div>
                        
                        <div class="mt-3 text-center">
                            <small>Noch kein Konto? <a href="/signup">Registrieren</a></small>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>