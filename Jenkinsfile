pipeline {
  agent { label 'maven' }

  options {
      timeout(time: 1, unit: 'HOURS')
      buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  triggers { cron('@daily') }

  stages {
      stage('prepare') {
          steps {
              checkout scm
          }
      }
      
      stage('build') {
          steps {
              sh 'mvn clean compile'
          }
      }
      stage('build') {
        steps {
          sh 'mvn -B clean compile'
        }
      }
  
      stage('test') {
        steps {
          sh 'mvn -B clean test'
        }
  
        post {
          always {
            junit '**/target/surefire-reports/TEST-*.xml'
          }
        }
      }

      stage('package') {
        steps {
          sh 'mvn -B -DskipTests=true clean package'
          script {
            currentBuild.result = 'SUCCESS'
          }
        }
      }
    }
  
    post {
      failure {
        slackSend color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Failure (<${env.BUILD_URL}|Open>)"
      }
      unstable {
        slackSend color: 'warning', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Unstable (<${env.BUILD_URL}|Open>)"
      }
      changed {
        script{
          if('SUCCESS'.equals(currentBuild.result)) {
            slackSend color: 'good', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Back to normal (<${env.BUILD_URL}|Open>)"
          }
        }
      }
    }
}