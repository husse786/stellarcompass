<script>
	import './styles.css'; // Bootstrap laden
	let { data, children } = $props();
    // Daten vom Server (+layout.server.js)
	// svelte-ignore state_referenced_locally
		let { user, isAuthenticated } = data;
</script>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
	<div class="container">
		<a class="navbar-brand" href="/">Stellar Compass</a>
		<!-- svelte-ignore a11y_consider_explicit_label -->
		<button
			class="navbar-toggler"
			type="button"
			data-bs-toggle="collapse"
			data-bs-target="#navbarNav"
		>
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarNav">
			<ul class="navbar-nav me-auto">
				<li class="nav-item">
					<a class="nav-link" href="/">Home</a>
				</li>
                
                {#if isAuthenticated}
					<li class="nav-item">
						<a class="nav-link" href="/dashboard">Dashboard</a>
					</li>
				{/if}
			</ul>
            
			<div class="d-flex">
				{#if isAuthenticated}
					<span class="navbar-text me-3 text-white">
						Hallo, {user.name || 'User'}
					</span>
                    <form action="/logout" method="POST" class="d-inline">
					    <button class="btn btn-outline-light btn-sm">Logout</button>
                    </form>
				{:else}
					<a href="/login" class="btn btn-light btn-sm">Login</a>
				{/if}
			</div>
		</div>
	</div>
</nav>

<main class="container mt-4">
	{@render children()}
</main>

<footer class="text-center mt-5 py-3 text-muted">
    <small>&copy; 2025 Stellar Compass - Education for Everyone</small>
</footer>