FROM openjdk

# command:  docker build -f ./performace-test-container/Dockerfile .
ENV fatjar_name=tradebot-game.jar

COPY ./build/libs/tradebot-game-1.0-SNAPSHOT-jmh.jar ./${fatjar_name}
USER 0

RUN mkdir /reports
RUN chmod 766 /reports
RUN chmod 755 ./${fatjar_name}
# for further configuration, refer to: https://github.com/guozheng/jmh-tutorial/blob/master/README.md
CMD java -jar $fatjar_name -rf csv -rff /reports/report-2.csv
# magic command
# docker run --cpus=2 -m 512m -v ~/IdeaProjects/tradebot-game/reports:/reports <image-id>
# use docker cp if you want manually copy files between container and host