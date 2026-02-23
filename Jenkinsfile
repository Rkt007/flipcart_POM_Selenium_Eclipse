pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_TYPE',
            choices: ['smoke', 'regression', 'others'],
            description: 'Select which Selenium TestNG tests to run'
        )
    }

    triggers {
        cron('H 2 * * *')
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/Rkt007/flipcart_POM_Selenium_Eclipse.git'
            }
        }

        stage('Verify Java & Maven') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
            }
        }

        stage('Clean & Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Run Selenium Tests') {
            steps {
                script {
                    def testType = params.TEST_TYPE ?: 'smoke'

                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {

                        if (testType == 'smoke') {
                            bat 'mvn test -Dgroups=smoke'
                        }
                        else if (testType == 'regression') {
                            bat 'mvn test -Dgroups=regression'
                        }
                        else {
                            bat 'mvn test'
                        }
                    }
                }
            }
        }
    }

    post {

        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'test-output/**', allowEmptyArchive: true
        }

        success {
            emailext(
                to: 'rahul.rkt007@gmail.com',
                subject: "Jenkins SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Hi Rahul,

Selenium build SUCCESS.

All tests passed.

Build URL:
${env.BUILD_URL}
"""
            )
        }

        unstable {
            emailext(
                to: 'rahul.rkt007@gmail.com',
                subject: "Jenkins UNSTABLE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Hi Rahul,

Some tests failed.

Please check TestNG report.

Build URL:
${env.BUILD_URL}
"""
            )
        }

        failure {
            emailext(
                to: 'rahul.rkt007@gmail.com',
                subject: "Jenkins FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Hi Rahul,

Selenium execution FAILED.

Check console logs.

Build URL:
${env.BUILD_URL}
"""
            )
        }
    }
}