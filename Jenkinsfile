@Library('sd')_
def kubeLabel = getKubeLabel()

pipeline {

  agent {
    kubernetes {
      label "${kubeLabel}"
      cloud 'Kube mwdevel'
      defaultContainer 'jnlp'
      inheritFrom 'ci-template'
    }
  }

  options {
      timeout(time: 1, unit: 'HOURS')
      buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  triggers { cron('@daily') }

  stages {
      stage('build') {
        steps {
          container('runner') {
            sh 'mvn -B clean compile'
          }
        }
      }
  
      stage('test') {
        steps {
          container('runner') {
            sh 'mvn -B clean test'
          }
        }
  
        post {
          always {
            junit '**/target/surefire-reports/TEST-*.xml'
          }
        }
      }

      stage ('checkstyle') {
        steps {
          container('runner') {
            sh "mvn checkstyle:check -Dcheckstyle.config.location=google_checks.xml"
            script {
              step([$class: 'hudson.plugins.checkstyle.CheckStylePublisher',
                pattern: '**/target/checkstyle-result.xml',
                healty: '20',
                unHealty: '100'])
            }
          }
        }
      }

      stage ('coverage') {
        steps {
          container('runner') {
            sh 'mvn cobertura:cobertura -Dcobertura.report.format=xml -DfailIfNoTests=false'
          }
        }
        post {
          always {
            step([$class: 'CoberturaPublisher', autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: '**/target/site/cobertura/coverage.xml', failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false])
          }
        }
      }

      stage('package') {
        steps {
          container('runner') {
            sh 'mvn -B -DskipTests=true clean package'
            script {
              currentBuild.result = 'SUCCESS'
            }
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
