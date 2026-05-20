# syntax=docker/dockerfile:1.7
# =============================================================================
#  Backend Dockerfile — Spring Boot (Java 17)
#  Multi-stage: Maven build → Slim JRE runtime
# =============================================================================

# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM amazoncorretto:17-alpine AS builder

WORKDIR /build

# Install Maven separately so package installation is cached independently.
RUN apk add --no-cache maven

# Cache dependency layer — copy POM first
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes). The cache mount keeps
# Maven artifacts across interrupted/repeated Docker builds.
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B -ntp

# Copy source and build
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -B -ntp

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM amazoncorretto:17-alpine AS runtime

LABEL maintainer="workflow-os" \
      description="Workflow OS Spring Boot Backend" \
      version="1.0.0"

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Own files as non-root user
RUN chown -R appuser:appgroup /app
USER appuser

# Spring Boot listens on 8080
EXPOSE 8080

# Health check — Docker will mark container healthy once Spring Boot is up
HEALTHCHECK --interval=15s --timeout=5s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.devtools.restart.enabled=false", \
    "-jar", "app.jar"]
