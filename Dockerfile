# ===== BUILD STAGE =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Dependency cache uchun avval faqat pom.xml copy qilamiz
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Keyin source code
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== RUN STAGE =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Jar faylni copy qilamiz
COPY --from=build /app/target/*.jar app.jar

# Port
EXPOSE 8080

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]