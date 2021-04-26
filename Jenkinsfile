@Library ('folio_jenkins_shared_libs') _

pipeline {

  environment {
    ORG_GRADLE_PROJECT_appName = 'mod-service-interaction'
    GRADLEW_OPTS = '--console plain --no-daemon'
    BUILD_DIR = "${env.WORKSPACE}/service"
    MD = "${env.WORKSPACE}/service/build/resources/main/okapi/ModuleDescriptor.json"
    doKubeDeploy = true

  }

  options {
    timeout(30)
    buildDiscarder(logRotator(numToKeepStr: '30'))
  }

  agent {
    node {
      label 'jenkins-agent-java11'
    }
  }

  stages {
    stage ('Setup') {
      steps {
        dir(env.BUILD_DIR) {
          script {
            def foliociLib = new org.folio.foliociCommands()
            def gradleVersion = foliociLib.gradleProperty('appVersion')

            env.name = env.ORG_GRADLE_PROJECT_appName
        
            // if release 
            if ( foliociLib.isRelease() ) {
              // make sure git tag and version match
              if ( foliociLib.tagMatch(gradleVersion) ) {
                env.isRelease = true 
                env.dockerRepo = 'folioorg'
                env.version = gradleVersion
              }
              else { 
                error('Git release tag and Maven version mismatch')
              }
            }
            else {
              env.dockerRepo = 'folioci'
              // build number is set in build.gradle
              env.version = "${gradleVersion}"
            }
          }
        }
        sendNotifications 'STARTED'  
      }
    }

    stage('Gradle Build') { 
      steps {
        dir(env.BUILD_DIR) {
          sh "./gradlew $env.GRADLEW_OPTS -PappVersion=${env.version} assemble"
        }
      }
    }
   
    stage('Build Docker') {
      steps {
        dir(env.WORKSPACE) {
          sh "docker build --pull=true --no-cache=true -t ${env.name}:${env.version} ."
        }
        // debug
        sh "cat $env.MD"
      } 
    }

    stage('Publish Docker Image') { 
      when { 
        anyOf {
          branch 'master'
          expression { return env.isRelease }
        }
      }
      steps {
        script {
          docker.withRegistry('https://index.docker.io/v1/', 'DockerHubIDJenkins') {
            sh "docker tag ${env.name}:${env.version} ${env.dockerRepo}/${env.name}:latest"
            sh "docker tag ${env.name}:${env.version} ${env.dockerRepo}/${env.name}:${env.version}"
            sh "docker push ${env.dockerRepo}/${env.name}:${env.version}"
            sh "docker push ${env.dockerRepo}/${env.name}:latest"
          }
        }
      }
    }

    stage('Publish Module Descriptor') {
      when {
        anyOf { 
          branch 'master'
          expression { return env.isRelease }
        }
      }
      steps {
        // md version number is handled in build.gradle
        // just take whats in there
        postModuleDescriptor(env.MD)
      }
    }

  } // end stages

  post {
    always {
      dockerCleanup()
      sendNotifications currentBuild.result 
    }
  }
}
         

