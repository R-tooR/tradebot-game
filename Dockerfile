# syntax=docker/dockerfile:1
FROM openjdk

ENV fatjar_name=tradebot-game.jar
# it has already properties files inside - that's Ok for the moment
COPY ./build/libs/tradebot-game-1.0-SNAPSHOT.jar ./${fatjar_name}

RUN chmod 755 ./${fatjar_name}
CMD java -jar $fatjar_name