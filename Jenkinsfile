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
                    [vaultKey: 'firebase_distribution_develop', envVar: 'firebase_distribution_develop'],
                    [vaultKey: 'firebase_distribution_test', envVar: 'firebase_distribution_test'],
                    [vaultKey: 'firebase_distribution_stg', envVar: 'firebase_distribution_stg']
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
// 			stage('Sonar Quality') {
// 				logger.stage()
// 				timeout(15) {
// 					withSonarQubeEnv('Seon SonarQube') {
// 						sh "./gradlew sonarqube"
// 					}
// 					waitForQualityGate abortPipeline: true
// 				}
// 			}
			if(env.BRANCH_NAME != 'develop' && env.BRANCH_NAME != 'master' && !env.BRANCH_NAME.startsWith('release/')) {
				stage('UI tests') {
					logger.stage()
					def approval_value = true
// 					timeout(5){
// 						def input_approve = input message: 'Do you want to execute UI tests?', ok: 'Submit', parameters: [choice(choices: ['YES', 'NO'], description: 'APPROVE this build to execute UI tests ?', name: 'APPROVE_TESTS')], submitterParameter: 'approving_submitter'
// 						if (input_approve.APPROVE_TESTS == 'YES') {
// 							approval_value = true
// 						}
// 					}
					if (approval_value == true) {
						stage('Generate APK Debug and testing'){
							logger.stage()
							timeout(10){
								sh "./gradlew assembleDebug --stacktrace"
								//sh "./gradlew assembleDebugAndroidTest --stacktrace"
								}
							logger.info("Archive APK")
							timeout(10){
                                sh "mv $WORKSPACE/app/build/outputs/apk/debug/app-debug.apk $WORKSPACE/app/build/outputs/apk/debug/app-debug-${BUILD_NUMBER}.apk"
                                archiveArtifacts "app/build/outputs/apk/debug/app-debug-${BUILD_NUMBER}.apk"
                            }
						}
						stage('Send APK to Firebase'){
						    logger.stage()
						    logger.info("Get Keystore")
						    timeout(10){
                                withVault(vaultSecrets: secrets) {
                                    sh """cat > $WORKSPACE/keystore.jks_64 <<  EOL\n$android_keystore\nEOL"""
                                    sh "base64 -d keystore.jks_64 > app/keystore.jks"
                                }
                            }
							logger.info("Send APK Develop to Firebase")
							timeout(10){
								withVault(vaultSecrets: secrets) {
									sh """cat > $WORKSPACE/firebase_distribution_develop.json_64 <<  EOL\n$firebase_distribution_develop\nEOL"""
									sh "base64 -d firebase_distribution_develop.json_64 > app/src/debug/fma-distribution.json"

									sh "./gradlew assembleDebug appDistributionUploadDebug --stacktrace"

									sh "rm $WORKSPACE/app/src/debug/fma-distribution.json"
								}
							}
							logger.info("Send APK Test to Firebase")
							timeout(10){
                                withVault(vaultSecrets: secrets) {
                                    sh """cat > $WORKSPACE/firebase_distribution_test.json_64 <<  EOL\n$firebase_distribution_test\nEOL"""
                                    sh "base64 -d firebase_distribution_test.json_64 > app/src/qaTest/fma-distribution.json"

                                    sh "./gradlew assembleQaTest appDistributionUploadQaTest --stacktrace"

                                    sh "rm $WORKSPACE/app/src/qaTest/fma-distribution.json"
                                }
                            }
						}
						stage('Sign APKs and run tests on firebase'){
							logger.stage()
							timeout(60){
								def pass = ""
								def alias = ""
								withVault(vaultSecrets: secrets) {
									sh """cat > $WORKSPACE/firebase_distribution_develop.json_64 <<  EOL\n$firebase_distribution_develop\nEOL"""
									sh "base64 -d firebase_distribution_develop.json_64 > fma-service-account.json"
									pass = "${env.KEYSTORE_PASSWORD}"
									alias = "${env.KEYSTORE_ALIAS}"
								}
								withEnv(["KEYSTORE_PASSWORD=$pass", "KEYSTORE_ALIAS=$alias"]) {
									sh "/home/user/android-sdk-linux/build-tools/28.0.3/apksigner sign --ks app/keystore.jks --ks-pass pass:$pass app/build/outputs/apk/debug/app-debug-${BUILD_NUMBER}.apk"
									//sh "/home/user/android-sdk-linux/build-tools/28.0.3/apksigner sign --ks app/keystore.jks --ks-pass pass:$pass app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
									sh "gcloud auth activate-service-account --key-file=fma-service-account.json"
									sh "gcloud config set project fma-dev-8d851"
									//sh "gcloud firebase test android run --timeout 20m --type instrumentation --app app/build/outputs/apk/debug/app-debug-${BUILD_NUMBER}.apk --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk --device model=x1q,version=29,locale=en,orientation=portrait --use-orchestrator --test-targets \"annotation com.safefleet.lawmobile.helpers.SmokeTest\""
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
					} else {
						exit 1
						currentBuild.result = 'FAILURE'
					}
				}
            }
            if(env.BRANCH_NAME.contains('OBSERVATIONS') || env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('release/')) {
                stage('Upload libraries'){
                    timeout(5){
                        withEnv(["VARIANT=SNAPSHOT"]) {
                            sh "./gradlew uploadArchives"
                        }
                    }
                }
            }
            if(env.BRANCH_NAME.contains('OBSERVATIONS') || env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('release/')){
                stage('Generate APK'){
                    logger.stage()
                    timeout(10){
                        withVault(vaultSecrets: secrets) {

                            sh """cat > $WORKSPACE/keystore.jks_64 <<  EOL\n$android_keystore\nEOL"""
                            sh "base64 -d keystore.jks_64 > app/keystore.jks"
                            //sh "./gradlew bundleRelease --stacktrace"
                            sh "./gradlew assemble --stacktrace"
                        }
                    }
                }
                stage('Archive APK'){
                 logger.stage()
                 timeout(10){
                 sh "mv $WORKSPACE/app/build/outputs/apk/release/app-release.apk $WORKSPACE/app/build/outputs/apk/release/app-release-${BUILD_NUMBER}.apk"
                  archiveArtifacts "app/build/outputs/apk/release/app-release-${BUILD_NUMBER}.apk"



                  }
                }

				if (env.BRANCH_NAME.startsWith('release/')){
					stage('Send APK Staging to Firebase'){
						logger.stage()
						timeout(10){
							withVault(vaultSecrets: secrets) {
								sh """cat > $WORKSPACE/firebase_distribution_stg.json_64 <<  EOL\n$firebase_distribution_stg\nEOL"""
								sh "base64 -d firebase_distribution_stg.json_64 > app/src/staging/fma-distribution.json"

								sh "./gradlew assembleStaging appDistributionUploadStaging --stacktrace"

								sh "rm $WORKSPACE/app/src/staging/fma-distribution.json"
							}
						}
					}
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
// 				stage('Archive AAB'){
// 					logger.stage()
// 					timeout(10){
// 						dir("app/build/outputs/bundle") {
// 							//sh "mv release/app-release.aab release/app-release-${BUILD_NUMBER}.aab"
// 							archiveArtifacts "release/app-release.aab"
// 						}
// 					}
// 				}
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