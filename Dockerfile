# ===== BUILD STAGE =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Dependency cache'ni saqlab qolish uchun (Build vaqtini tejaydi)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Source code'ni nusxalash va build qilish
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== RUN STAGE =====
# Alpine varianti yengil va xavfsiz
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# JAR faylni nusxalash (wildcard o'rniga aniqroq target ishlatamiz)
COPY --from=build /app/target/*.jar app.jar

# application.yml dagi port bilan bir xil bo'lishi kerak
EXPOSE 8080

# Java optimizatsiyasi bilan ishga tushirish
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]