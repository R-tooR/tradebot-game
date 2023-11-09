# Tradebot game

Running Sonar analysis locally: ./gradlew sonar -Dsonar.token=<token>

Pushing docker image:
docker build <imagename>
docker image tag <imagename> userName/repoName:tag
docker push userName/repoName:tag