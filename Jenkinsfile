pipeline {
    agent { docker 'maven:3.3.9' }
    stages {
        stage('build') {
            steps {
                sh './gradlew build'
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        failure {
            mail to: 'goran@technipelago.se',
            subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
            body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}