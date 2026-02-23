pipeline {

    agent {
    docker {
        image '102783063324.dkr.ecr.eu-north-1.amazonaws.com/flipcart-pom-framework-selenium-eclipse:latest'
        args '-u 1000:1000 --ipc=host --entrypoint=""'
        reuseNode true
        alwaysPull true
    }
}

    options {
        timestamps()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
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
            description: 'Select which test suite to run'
        )
    }

    environment {
        S3_BUCKET = "rahul-selenium-reports-2026"
        BUILD_FOLDER = "build-${BUILD_NUMBER}"
        AWS_DEFAULT_REGION = "eu-north-1"
        AWS_REGION = "eu-north-1"
        AWS_ACCESS_KEY_ID = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')

        // 🔥 Force disable DEBUG logs globally
        JAVA_TOOL_OPTIONS = "-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog"
    }

    stages {

        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Determine Test Suite') {
            steps {
                script {

                    if (env.BUILD_CAUSE_TIMERTRIGGER) {

                        def day = sh(script: "date +%u", returnStdout: true).trim()

                        if (day == "6") {
                            env.MVN_COMMAND = "mvn -B -q clean test -Dgroups=regression -Dlogging.level.root=ERROR"
                        } else {
                            env.MVN_COMMAND = "mvn -B -q clean test -Dgroups=smoke -Dlogging.level.root=ERROR"
                        }

                    } else {

                        if (params.TEST_TYPE == "smoke") {
                            env.MVN_COMMAND = "mvn -B -q clean test -Dgroups=smoke -Dlogging.level.root=ERROR"
                        }
                        else if (params.TEST_TYPE == "regression") {
                            env.MVN_COMMAND = "mvn -B -q clean test -Dgroups=regression -Dlogging.level.root=ERROR"
                        }
                        else {
                            env.MVN_COMMAND = "mvn -B -q clean test -Dlogging.level.root=ERROR"
                        }
                    }

                    echo "Running: ${env.MVN_COMMAND}"
                }
            }
        }

        stage('Run Selenium Tests') {
            steps {
                script {

                    def exitCode = sh(
                        script: "${env.MVN_COMMAND}",
                        returnStatus: true
                    )

                    // Generate Allure quietly
                    sh "mvn -B -q allure:report || true"

                    if (exitCode != 0) {
                        currentBuild.result = 'FAILURE'
                    }
                }
            }
        }

        stage('Upload Allure Report to S3') {
            steps {
                sh '''
                    if [ -d target/site/allure-maven-plugin ]; then
                        echo "Uploading Allure report to S3..."
                        aws s3 sync target/site/allure-maven-plugin/ \
                        s3://${S3_BUCKET}/${BUILD_FOLDER}/ --delete
                    else
                        echo "Allure report folder not found. Skipping upload."
                    fi
                '''
            }
        }
    }

    post {
        always {
            echo "=========================================="
            echo "Allure Report URL:"
            echo "https://${S3_BUCKET}.s3.${AWS_REGION}.amazonaws.com/${BUILD_FOLDER}/index.html"
            echo "=========================================="
        }
    }
}