# Login Service

Purpose of this service is to register user and allow login based on username and password. JWT token is returned

# CI/CD

brew install Jenkins-lts<br>
brew services start jenkins-lts<br>
cat ~/.jenkins/secrets/initialAdminPassword<br>
http://localhost:8080/job/test/job/master/

# SonarCube

docker pull SonarQube<br>
docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest<br>
http://localhost:9000/dashboard?id=Login-Service

# Swagger-Docs

https://localhost:8083/swagger-ui/#/



