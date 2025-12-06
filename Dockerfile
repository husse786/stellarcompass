FROM eclipse-temurin:21-jdk-jammy

# Install dependencies (Node.js and Supervisor for process management)
RUN apt-get update && apt-get install -y supervisor curl \
    && curl -sL https://deb.nodesource.com/setup_22.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/app

# Copy backend files
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
COPY src src

# Copy frontend files
COPY frontend frontend

# 1. Build frontend
RUN cd frontend && npm ci && npx svelte-kit sync && npm run build

# 2. Build backend
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw package -DskipTests

# 3. Configuration: Use supervisor to start frontend and backend
RUN cat > /etc/supervisor/conf.d/supervisord.conf <<'EOF'
[supervisord]
nodaemon=true
user=root

[program:backend]
command=java -jar /usr/src/app/target/stellarcompass-0.0.1-SNAPSHOT.jar
directory=/usr/src/app
autostart=true
autorestart=true
stdout_logfile=/dev/stdout
stdout_logfile_maxbytes=0
stderr_logfile=/dev/stderr
stderr_logfile_maxbytes=0

[program:frontend]
directory=/usr/src/app/frontend
command=node build
autostart=true
autorestart=true
environment=PORT=3000,ORIGIN=https://stellarcompass.azurewebsites.net
stdout_logfile=/dev/stdout
stdout_logfile_maxbytes=0
stderr_logfile=/dev/stderr
stderr_logfile_maxbytes=0
EOF

EXPOSE 3000 8080
ENV NODE_ENV=production

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]