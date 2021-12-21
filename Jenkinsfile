pipeline {
   agent any
   stages {
      stage('Build') {
         steps {
            sh "./mvnw clean install -DskipTests"
         }
      }
      stage('Test') {
               steps {
                  sh "./mvnw test surefire-report:report"
               }
               post {
                  success {
                     junit '**/target/surefire-reports/TEST-*.xml'
                     archiveArtifacts 'target/*.jar'
                  }
               }
      }
      stage('Sonar') {
                     steps {
                        sh "./mvnw verify sonar:sonar -Dsonar.projectKey=Login-Service -Dsonar.host.url=http://localhost:9000 -Dsonar.login=a083cb08c453b7fcb9ff659487e8f98b433b01fb"
                     }
      }
   }
}