node {
  stage 'Git Clone'
  checkout scm
  stage 'Maven Compile'
  sh 'mvn clean deploy -U'
  stage 'Jenkins Archive'
  step([$class: 'ArtifactArchiver', artifacts: 'target/*.jar', fingerprint: true])
}
