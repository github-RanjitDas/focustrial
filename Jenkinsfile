node ('docker-builds-slave') {
    def slackChannel = '#law-mobile-alerts'

    try {
        library identifier: "${env.DEFAULT_SHARED_LIBS}",
                retriever: modernSCM([$class: 'GitSCMSource', remote: "${env.DEFAULT_SHARED_LIBS_REPO}"])

        pipelineProps.defaultBuildMultibranchProperties()

        stage('Checkout') {
            logger.stage()
            timeout(10) {
                checkout scm
            }

            slackUtils.notifyBuild('STARTED', slackChannel)
        }
        stage('Login to AWS') {
            logger.stage()
            timeout(5) {
                 awsUtils.loginToAWS()
            }
        }
        docker.image("245255707803.dkr.ecr.us-east-1.amazonaws.com/android-sdk-seon:sdk29-gradle5.6.4").inside {

            stage('Clean builds'){
                logger.stage()
                timeout(5){
                    sh "gradle clean"
                }
            }

            stage('Build project'){
                logger.stage()
                timeout(15) {
                    sh "gradle buildDebug --stacktrace"
                }
            }

            stage('Unit Tests') {
                logger.stage()
                timeout(5) {
                    sh "gradle testDebugUnitTestCoverage --stacktrace"
                }
            }
            if(env.BRANCH_NAME != 'develop' && env.BRANCH_NAME != 'master'){
                stage('Copy Mutation results from last successful pipeline') {
                    logger.stage()
                    if (currentBuild.previousBuild) {
                        try {
                            copyArtifacts(projectName: env.JOB_NAME, selector: lastSuccessful())
                        } catch(err) {
                            echo("An error occurred while copying results")
                        }
                    }
                }
            }
            stage('Mutation Tests') {
                logger.stage()
                timeout(20) {
                    sh "gradle pitestDebug --stacktrace"
                    archiveArtifacts "presentation/build/pitHistory.txt"
                    archiveArtifacts "domain/build/pitHistory.txt"
                    archiveArtifacts "data/build/pitHistory.txt"
                    sh "./merge_mutation_reports.sh"
                }
            }

            stage('Sonar Quality') {
                logger.stage()
                timeout(15) {
                    withSonarQubeEnv('Seon SonarQube') {
                        sh "gradle sonarqube"
                    }
                    waitForQualityGate abortPipeline: true
                }
            }

            if(env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'master'){
                stage('Generate APK'){
                    logger.stage()
                    timeout(10){
                        sh "gradle assembleDebug --stacktrace"
                    }
                }
                stage('Archive APK'){
                    logger.stage()
                    timeout(10){
                        dir("app/build/outputs/apk") {
                            sh "mv debug/app-debug.apk debug/app-debug-${BUILD_NUMBER}.apk"
                            archiveArtifacts "debug/app-debug-${BUILD_NUMBER}.apk"
                        }
                    }
                }
            }
        }
    } catch (e) {
        currentBuild.result = 'FAILURE'
        throw e
    } finally {
        stage('Notify') {
            slackUtils.notifyBuild(currentBuild.result, slackChannel)
        }

        stage('Clean Up') {
            cleanWs()
        }
    }
}