#!groovy​

node {
    stage('checkout') {
      checkout scm
    }
   stage('coverage') {
     sh "./gradlew cobertura"
   }
   stage('build') {
     sh "./gradlew build"
   }
   stage('result') {
      junit '**/build/test-results/test/*.xml'
      archive 'build/libs/*.jar'
   }
}