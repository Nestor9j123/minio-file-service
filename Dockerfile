# Multi-stage build for optimized production image
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (for better layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# List the target directory to debug
RUN ls -la /app/target/

# Production stage
FROM eclipse-temurin:21-jre-alpine

# Install necessary packages for PDF processing
RUN apk add --no-cache \
    fontconfig \
    ttf-dejavu \
    && rm -rf /var/cache/apk/*

# Create app directory and user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

# Copy the built JAR from build stage with explicit name
COPY --from=build /app/target/minio-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to appuser
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Railway uses PORT environment variable
EXPOSE $PORT

# Health check optimized for Railway
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Set JVM options optimized for Railway free tier (512MB RAM limit)
ENV JAVA_OPTS="-Xms128m -Xmx400m -XX:+UseG1GC -XX:+UseContainerSupport"

# Run the application with Railway's PORT variable
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]
