node {
  try {
    stage 'checkout project'
    git url: 'https://github.com/onkibot/onkibot-backend.git'

    stage 'check env'
    sh "mvn -v"
    sh "java -version"

    stage 'gradle dependencies'
    sh 'export TERM="dumb"; if [ -e ./gradlew ]; then ./gradlew dependencies;else gradle dependencies;fi'

    stage 'gradle test'
    sh 'export TERM="dumb"; if [ -e ./gradlew ]; then ./gradlew test;else gradle test;fi'

    stage 'Artifact'
    step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
  } catch(e) {
    slackSend channel: '#integration', color: 'danger', message: "fail ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'agileworks-tw', token: 'JhXFKEl6cBFoQ4v52BEJw9Mr'
    throw e;
  }
}
