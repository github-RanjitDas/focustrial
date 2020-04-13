node ('master') {
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
    }

    catch (e) {
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