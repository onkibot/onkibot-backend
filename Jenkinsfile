pipeline {
  agent any
  stages {
    stage('Checkout Project') {
      steps {
        git url: 'https://github.com/onkibot/onkibot-backend.git'
      }
    }

    stage('Check Envoirment') {
      steps {
        sh 'java -version'
      }
    }

    stage('Gradle Dependencies') {
      steps {
        sh '''
        export TERM="dumb"
        if [ -e ./gradlew ]; then ./gradlew dependencies;else gradle dependencies;fi
        '''
      }
    }

    stage('Gradle Test') {
      steps {
        sh '''
        export TERM="dumb"
        if [ -e ./gradlew ]; then ./gradlew test;else gradle test;fi
        '''
      }
    }

    stage('Build Docker Services') {
      steps {
        sh 'docker-compose down'
        sh 'docker-compose up --force-recreate -d'
      }
    }
  }
}

