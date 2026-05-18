# =============================================================================
#  Backend Dockerfile — Spring Boot (Java 17)
#  Multi-stage: Maven build → Slim JRE runtime
# =============================================================================

# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM amazoncorretto:17-alpine AS builder

WORKDIR /build

# Cache dependency layer — copy POM first
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B

# Copy source and build
COPY src/ src/
RUN mvn clean package -DskipTests -B

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
