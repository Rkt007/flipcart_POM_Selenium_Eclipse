pipeline {
    agent any

    tools {
        maven 'Maven'
    }

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

        stage('Verify Tools') {
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
    }
}