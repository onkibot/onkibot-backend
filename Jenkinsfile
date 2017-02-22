pipeline {
  agent { docker 'java:8' }
  stages {
    stage('checkout project') {
      steps {
        git url: 'https://github.com/onkibot/onkibot-backend.git'
      }
    }

    stage('check env') {
      steps {
        sh 'java -version'
      }
    }

    stage('gradle dependencies') {
      steps {
        sh '''
        export TERM="dumb"
        if [ -e ./gradlew ]; then ./gradlew dependencies;else gradle dependencies;fi
        '''
      }
    }

    stage('gradle test') {
      steps {
        sh '''
        export TERM="dumb"
        if [ -e ./gradlew ]; then ./gradlew test;else gradle test;fi
        '''
      }
    }
  }
}
