node {
    /*stage('Checkout') {
      git url: 'https://github.com/technipelago/sequence-rest-service.git'
    }*/
   stage('build') {
     sh "./gradlew build"
   }
   stage('result') {
      junit '**/build/test-results/test/*.xml'
      archive 'build/libs/*.jar'
   }
}