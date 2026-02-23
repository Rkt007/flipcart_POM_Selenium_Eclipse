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

    tools {
        maven 'Maven'      // Configure this in Jenkins Global Tool Config
        jdk 'JDK17'        // Configure this in Jenkins Global Tool Config
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/Rkt007/flipcart_POM_Selenium_Eclipse.git'
            }
        }

        stage('Build Project') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Run Selenium Tests') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    script {
                        def testType = params.TEST_TYPE ?: 'smoke'

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
            junit 'target/surefire-reports/*.xml'

            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'test-output/**', allowEmptyArchive: true
        }

        success {
            emailext(
                to: 'rahul.rkt007@gmail.com',
                subject: "✅ Jenkins SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Hi Rahul,

Jenkins build SUCCESS ✅

All Selenium tests passed.

Build URL:
${env.BUILD_URL}
"""
            )
        }

        unstable {
            emailext(
                to: 'rahul.rkt007@gmail.com',
                subject: "⚠️ Jenkins UNSTABLE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Hi Rahul,

Some Selenium tests failed ⚠️

Please check TestNG report.

Build URL:
${env.BUILD_URL}
"""
            )
        }

        failure {
            emailext(
                to: 'rahul.rkt007@gmail.com',
                subject: "❌ Jenkins FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Hi Rahul,

Selenium execution FAILED ❌

Please check console logs and reports.

Build URL:
${env.BUILD_URL}
"""
            )
        }
    }
}