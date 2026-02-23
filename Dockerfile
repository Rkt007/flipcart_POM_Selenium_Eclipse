# Use official Maven image with Java 17
FROM maven:3.9.6-eclipse-temurin-17

# Set working directory
WORKDIR /app

# Install required packages
RUN apt-get update && \
    apt-get install -y wget curl unzip gnupg2 software-properties-common && \
    rm -rf /var/lib/apt/lists/*

# ----------------------------
# Install Google Chrome
# ----------------------------
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# ----------------------------
# Install ChromeDriver
# ----------------------------
RUN CHROME_VERSION=$(google-chrome --version | awk '{print $3}') && \
    DRIVER_VERSION=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_VERSION%%.*}") && \
    wget -q "https://chromedriver.storage.googleapis.com/${DRIVER_VERSION}/chromedriver_linux64.zip" && \
    unzip chromedriver_linux64.zip && \
    mv chromedriver /usr/local/bin/ && \
    chmod +x /usr/local/bin/chromedriver && \
    rm chromedriver_linux64.zip

# ----------------------------
# Install AWS CLI v2
# ----------------------------
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install && \
    rm -rf aws awscliv2.zip

# ----------------------------
# Install Allure CLI
# ----------------------------
RUN wget https://github.com/allure-framework/allure2/releases/download/2.29.0/allure-2.29.0.tgz && \
    tar -xzf allure-2.29.0.tgz && \
    mv allure-2.29.0 /opt/allure && \
    ln -s /opt/allure/bin/allure /usr/bin/allure && \
    rm allure-2.29.0.tgz

# Verify installations
RUN java -version && \
    mvn -version && \
    google-chrome --version && \
    chromedriver --version && \
    aws --version && \
    allure --version

# Copy project files
COPY . .

# Default command
CMD ["mvn", "-B", "-ntp", "clean", "test"]