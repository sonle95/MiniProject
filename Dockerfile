FROM gradle:8.8-jdk21 AS builder

WORKDIR /app

# Copy toàn bộ source vào container
COPY . .

# Đảm bảo gradlew có quyền thực thi
RUN chmod +x gradlew

# Build jar, bỏ qua test
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine-3.22
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

EXPOSE 8080