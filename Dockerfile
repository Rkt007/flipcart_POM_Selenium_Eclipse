FROM maven:3.9.6-eclipse-temurin-17

RUN apt-get update && apt-get install -y \
    wget unzip curl gnupg \
    libnss3 libatk-bridge2.0-0 libgtk-3-0 \
    libxss1 libasound2 \
    --no-install-recommends

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

CMD ["mvn", "test"]