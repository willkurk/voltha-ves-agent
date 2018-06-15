mvn package
mvn install dockerfile:build
docker tag osam/ves-agent localhost:5000/ves-agent
docker push localhost:5000/ves-agent
