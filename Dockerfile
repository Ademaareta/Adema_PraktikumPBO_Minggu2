# Tahap 1: kompilasi dan pengemasan aplikasi.
FROM gradle:8.10.2-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle installDist --no-daemon

# Tahap 2: image runtime lebih ringan, hanya membawa hasil aplikasi.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/install/simpus/ ./
RUN mkdir -p /app/data
ENTRYPOINT ["/app/bin/simpus"]
