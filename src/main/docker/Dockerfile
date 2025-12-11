# ============================================================
# 1. BUILD STAGE — Maven + JDK 17
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copier la configuration Maven (permet de cacher les deps)
COPY ../../../pom.xml mvnw ./
COPY ../../../.mvn .mvn

# Télécharger les dépendances MAVEN (cache optimal)
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY ../.. src

COPY ../../../sonar-project.properties sonar-project.properties

# Compilation en mode production
RUN ./mvnw -Pprod -DskipTests -Dsonar.skip=true package -B



# ============================================================
# 2. RUNTIME STAGE — JRE 17 Slim (image légère)
# ============================================================
FROM eclipse-temurin:17-jre-jammy

ENV APP_HOME=/app
WORKDIR $APP_HOME

# Créer un user non-root
RUN groupadd --system ondmoney && \
    useradd --system --create-home --gid ondmoney ondmoney

# Copier le jar compilé
COPY --from=build /workspace/target/*.jar app.jar

# Donner les permissions
RUN chown ondmoney:ondmoney app.jar

USER ondmoney

# Exposer le port standard JHipster
EXPOSE 8080

# Variables JVM configurables
ENV JAVA_OPTS="\
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75 \
  -XX:InitialRAMPercentage=25 \
"

# Support de la config externe
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
