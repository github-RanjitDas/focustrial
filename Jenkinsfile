node('jenkins-builds-slave') {
    def slackChannel = '#law-mobile-alerts'
    def slackPSLChannel = '#law-mobile-psl'
    try {
        library identifier: "${env.DEFAULT_SHARED_LIBS}",
                retriever: modernSCM([$class: 'GitSCMSource', remote: "${env.DEFAULT_SHARED_LIBS_REPO}"])
        pipelineProps.defaultBuildMultibranchProperties()
        def secrets = [[
                  path: "/secret-dev/safefleet1",
                  engineVersion: '2',
                  secretValues: [
                    [vaultKey: 'credential_google_x1', envVar: 'credential_google_x1'],
                    [vaultKey: 'android_keystore', envVar: 'android_keystore'],
                    [vaultKey: 'keystore_alias', envVar: 'KEYSTORE_ALIAS'],
                    [vaultKey: 'keystore_password', envVar: 'KEYSTORE_PASSWORD'],
                    [vaultKey: 'firebase_testlab', envVar: 'firebase_testlab']
                  ]
        ]]
        def imageDocker = "245255707803.dkr.ecr.us-east-1.amazonaws.com/android-sdk-seon:sdk29-gradle6.0.0-fastlane"
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
        docker.image(imageDocker).inside('--user root') {
            stage('Clean builds'){
                logger.stage()
                timeout(5){
                    sh "./gradlew clean"
                }
            }
            stage('Kotlin format check') {
                logger.stage()
                timeout(5) {
                    sh "./gradlew ktlint"
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
                timeout(7) {
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
                stage('Generate APKs for testing'){
                    logger.stage()
                    timeout(10){
                        sh "./gradlew assembleDebug --stacktrace"
                        sh "./gradlew assembleDebugAndroidTest --stacktrace"
                    }
                }
                stage('Archive APK'){
                    logger.stage()
                    timeout(10){
                        dir("app/build/outputs/apk") {
                            //sh "mv debug/app-debug.apk debug/app-debug-${BUILD_NUMBER}.apk"
                            archiveArtifacts "debug/app-debug.apk"
                        }
                    }
                }
                stage('Upload libraries'){
                    timeout(5){
                        withEnv(["VARIANT=SNAPSHOT"]) {
                            sh "./gradlew uploadArchives"
                        }
                    }
                }
                stage('Sign APKs and run tests on firebase'){
                    logger.stage()
                    timeout(60){
                        def pass = ""
                        def alias = ""
                        withVault(vaultSecrets: secrets) {
                            sh """cat > $WORKSPACE/keystore.jks_64 <<  EOL\n$android_keystore\nEOL"""
                            sh "base64 -d keystore.jks_64 > app/keystore.jks"
                            sh """cat > $WORKSPACE/firebase_testlab.json_64 <<  EOL\n$firebase_testlab\nEOL"""
                            sh "base64 -d firebase_testlab.json_64 > firebase_testlab.json"
                            pass = "${env.KEYSTORE_PASSWORD}"
                            alias = "${env.KEYSTORE_ALIAS}"
                        }
                        withEnv(["KEYSTORE_PASSWORD=$pass", "KEYSTORE_ALIAS=$alias"]) {
                            sh "/home/user/android-sdk-linux/build-tools/28.0.3/apksigner sign --ks app/keystore.jks --ks-pass pass:$pass app/build/outputs/apk/debug/app-debug.apk"
                            sh "/home/user/android-sdk-linux/build-tools/28.0.3/apksigner sign --ks app/keystore.jks --ks-pass pass:$pass app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
                            sh "./gradlew bundleRelease --stacktrace"
                            sh "gcloud auth activate-service-account --key-file=firebase_testlab.json"
                            sh "gcloud config set project fma-analytics"
                            sh "gcloud firebase test android run --timeout 20m --type instrumentation --app app/build/outputs/apk/debug/app-debug.apk --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk --device model=flame,version=29,locale=en,orientation=portrait --use-orchestrator --test-targets \"annotation com.safefleet.lawmobile.helpers.SmokeTest\""
                        }
                    }
                }
            }
            if(env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('release/')){
                stage('Generate AAB'){
                    logger.stage()
                    timeout(10){
                        withVault(vaultSecrets: secrets) {
                            sh """cat > $WORKSPACE/keystore.jks_64 <<  EOL\n$android_keystore\nEOL"""
                            sh "base64 -d keystore.jks_64 > app/keystore.jks"
                            sh "./gradlew bundleRelease --stacktrace"
                        }
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
                    stage('Clean credentials Google'){
                        logger.stage()
                        timeout(10){
                            sh "rm $WORKSPACE/credentials.json_64"
                            sh "rm $WORKSPACE/app/credentials.json"
                        }
                    }
                }
                stage('Archive AAB'){
                    logger.stage()
                    timeout(10){
                        dir("app/build/outputs/bundle") {
                            //sh "mv release/app-release.aab release/app-release-${BUILD_NUMBER}.aab"
                            archiveArtifacts "release/app-release.aab"
                        }
                    }
                }
                stage('Upload libraries'){
                    timeout(5){
                        withEnv(["VARIANT=RELEASE"]) {
                            sh "./gradlew uploadArchives"
                        }
                    }
                }
                stage('Clean credentials Keystore'){
                    logger.stage()
                    timeout(10){
                        sh "rm $WORKSPACE/keystore.jks_64"
                        sh "rm $WORKSPACE/app/keystore.jks"
                    }
                }
            }
        }
    } catch (e) {
        currentBuild.result = 'FAILURE'
         if(env.BRANCH_NAME == 'develop') {
            slackUtils.notifyBuild('Failed commit on develop branch', slackPSLChannel)
         }
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