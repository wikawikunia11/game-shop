FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
RUN chmod +x gradlew

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon


FROM eclipse-temurin:25-jre-alpine
WORKDIR /product-app

COPY --from=build /app/build/libs/*.jar product-app.jar

EXPOSE 8081

CMD ["java", "-jar", "product-app.jar"]