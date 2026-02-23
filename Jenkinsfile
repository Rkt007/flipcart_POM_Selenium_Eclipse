pipeline {

    agent {
        docker {
            image '102783063324.dkr.ecr.eu-north-1.amazonaws.com/selenium-testng-framework:latest'
            args '-u root --ipc=host --entrypoint=""'
            reuseNode true
        }
    }

    options {
        timestamps()
    }

    triggers {
        cron('''
            H 9 * * 1-5
            H 11 * * 6
        ''')
    }

    parameters {
        choice(
            name: 'TEST_TYPE',
            choices: ['smoke', 'regression', 'all'],
            description: 'Select which test suite to run (Manual Trigger Only)'
        )
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

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Determine Test Suite') {
            steps {
                script {

                    if (env.BUILD_CAUSE_TIMERTRIGGER) {

                        echo "Scheduled run detected"

                        def day = sh(script: "date +%u", returnStdout: true).trim()

                        if (day == "6") {
                            env.MVN_COMMAND = "mvn test -Dgroups=regression"
                        } else {
                            env.MVN_COMMAND = "mvn test -Dgroups=smoke"
                        }

                    } else {

                        echo "Manual run detected"

                        if (params.TEST_TYPE == "smoke") {
                            env.MVN_COMMAND = "mvn test -Dgroups=smoke"
                        }
                        else if (params.TEST_TYPE == "regression") {
                            env.MVN_COMMAND = "mvn test -Dgroups=regression"
                        }
                        else {
                            env.MVN_COMMAND = "mvn test"
                        }
                    }
                }
            }
        }

        stage('Run Selenium Tests') {
            steps {
                script {

                    def exitCode = sh(
                        script: """
                            echo "Running: ${env.MVN_COMMAND}"
                            ${env.MVN_COMMAND}
                        """,
                        returnStatus: true
                    )

                    sh """
                        echo "Generating Allure Report..."
                        mvn allure:report || true
                    """

                    if (exitCode != 0) {
                        currentBuild.result = 'FAILURE'
                    }
                }
            }
        }

        stage('Upload Allure Report to S3') {
            steps {
                sh '''
                    echo "Uploading Allure report to S3..."
                    aws s3 sync target/site/allure-maven-plugin/ \
                        s3://${S3_BUCKET}/${BUILD_FOLDER}/ --delete
                '''
            }
        }
    }

    post {
        always {
            echo "=============================="
            echo "Allure report available at:"
            echo "https://${env.S3_BUCKET}.s3.${env.AWS_REGION}.amazonaws.com/${env.BUILD_FOLDER}/index.html"
            echo "=============================="
        }
    }
}