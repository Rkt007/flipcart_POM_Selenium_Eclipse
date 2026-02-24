pipeline {

    agent {
        docker {
            image '102783063324.dkr.ecr.eu-north-1.amazonaws.com/flipcart-pom-framework-selenium-eclipse:latest'
            args '--ipc=host'
            reuseNode true
            alwaysPull false
        }
    }

    options {
        timestamps()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        S3_BUCKET = "rahul-selenium-reports-2026"
        BUILD_FOLDER = "build-${BUILD_NUMBER}"
        AWS_DEFAULT_REGION = "eu-north-1"
        AWS_REGION = "eu-north-1"
        AWS_ACCESS_KEY_ID = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
    }

    stages {

        stage('Clean Workspace Safely') {
            steps {
                cleanWs(deleteDirs: true, disableDeferredWipeout: true)
            }
        }

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
                sh "mvn -B clean test"
                sh "mvn -B allure:report || true"
            }
        }

        stage('Upload Allure Report') {
            steps {
                sh '''
                    if [ -d target/site/allure-maven-plugin ]; then
                        aws s3 sync target/site/allure-maven-plugin/ \
                        s3://${S3_BUCKET}/${BUILD_FOLDER}/ --delete
                    fi
                '''
            }
        }
    }

    post {
        always {
            script {
                echo "=========================================="
                echo "Allure Report URL:"
                echo "https://${env.S3_BUCKET}.s3.${env.AWS_REGION}.amazonaws.com/${env.BUILD_FOLDER}/index.html"
                echo "=========================================="
            }
        }
    }
}