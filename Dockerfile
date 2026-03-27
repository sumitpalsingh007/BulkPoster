# ── Stage 1: build fat JAR ──────────────────────────────────────────────────
FROM eclipse-temurin:11-jdk-jammy AS builder

WORKDIR /build
COPY pom.xml .
# Download dependencies first (cached layer)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: runtime image with Google Chrome ────────────────────────────────
FROM eclipse-temurin:11-jre-jammy

# Install Google Chrome (stable)
RUN apt-get update && apt-get install -y \
    wget gnupg2 apt-transport-https ca-certificates \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub \
        | gpg --dearmor -o /usr/share/keyrings/google-linux-signing-key.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux-signing-key.gpg] \
       http://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# WebDriverManager writes ChromeDriver to ~/.cache – give it a writable home
ENV HOME=/app
WORKDIR /app

COPY --from=builder /build/target/my-linkedin-poster-1.0-SNAPSHOT.jar app.jar

# Cloud Run Jobs: container must exit 0 when done
CMD ["java", "-jar", "app.jar"]
