node ('docker-builds-slave') {
    def slackChannel = '#law-mobile-alerts'

    try {
        library identifier: "${env.DEFAULT_SHARED_LIBS}",
                retriever: modernSCM([$class: 'GitSCMSource', remote: "${env.DEFAULT_SHARED_LIBS_REPO}"])

        pipelineProps.defaultBuildMultibranchProperties()
        def secrets = [[
                  path: "/secret-dev/safe_fleet_x1",
                  engineVersion: '2',
                  secretValues: [
                    [vaultKey: 'credential_google_x1', envVar: 'credential_google_x1']
                  ]]
         ]

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
        docker.image("245255707803.dkr.ecr.us-east-1.amazonaws.com/android-sdk-seon:sdk29-gradle5.6.4-fastlane").inside {

            stage('Clean builds'){
                logger.stage()
                timeout(5){
                    sh "./gradlew clean"
                }
            }

            stage('Build project'){
                logger.stage()
                timeout(15) {
                    sh "./gradlew buildDebug --stacktrace"
                }
            }

            stage('Unit Tests') {
                logger.stage()
                timeout(5) {
                    sh "./gradlew testDebugUnitTestCoverage --stacktrace"
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
                    sh "./gradlew pitestDebug --stacktrace"
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
                        sh "./gradlew sonarqube"
                    }
                    waitForQualityGate abortPipeline: true
                }
            }

            if(env.BRANCH_NAME == 'develop') {
                stage('Generate APK'){
                    logger.stage()
                    timeout(10){
                        sh "./gradlew assembleDebug --stacktrace"
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

            if(env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('release/')){
                stage('Generate AAB'){
                    logger.stage()
                    timeout(10){
                        withVault(vaultSecrets: [[path: "jenkins/lawmobile/android", secretValues: [[vaultKey: 'android-keystore', envVar: 'android_keystore']]]]) {
                            sh """cat > $WORKSPACE/keystore.jks_64 <<  EOL\n$android_keystore\nEOL"""
                            sh "base64 -d keystore.jks_64 > app/keystore.jks"
                        }
                        sh "./gradlew bundleRelease --stacktrace"
                    }
                }

                if (env.BRANCH_NAME.startsWith('release/')){
                    stage('Update to play store internal'){
                        logger.stage()
                        timeout(10){
                            withVault(vaultSecrets: secrets) {
                                sh """cat > $WORKSPACE/credentials.json_64 <<  EOL\n$credential_google_x1\nEOL"""
                                sh "base64 -d credentials.json_64 > app/credentials.json"
                                sh 'fastlane deploy'
                                slackUtils.notifyBuild("New application has been upload on internal test", slackChannel)
                            }
                        }
                    }
                }

                stage('Archive AAB'){
                    logger.stage()
                    timeout(10){
                        dir("app/build/outputs/bundle") {
                            sh "mv release/app-release.aab release/app-release-${BUILD_NUMBER}.aab"
                            archiveArtifacts "release/app-release-${BUILD_NUMBER}.aab"
                        }
                    }
                }

                stage('Clean credentials'){
                    logger.stage()
                    timeout(10){
                        sh "rm $WORKSPACE/keystore.jks_64"
                        sh "rm $WORKSPACE/app/keystore.jks"
                        sh "rm $WORKSPACE/credentials.json_64"
                        sh "rm $WORKSPACE/app/credentials.json"
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