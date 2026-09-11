# 使用Maven镜像构建
FROM maven:3.9-eclipse-temurin-24 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:24-jre

WORKDIR /app

COPY --from=builder /app/target/restaurant-backend-1.0.0.jar app.jar

EXPOSE 8367

ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
