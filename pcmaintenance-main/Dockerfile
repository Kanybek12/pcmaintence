FROM adoptopenjdk/openjdk11

ARG JAR_FILE=target/PCMaintanence.jar

ADD ${JAR_FILE} /usr/src/app/PCMaintanence.jar

WORKDIR /usr/src/app

ENTRYPOINT ["java","-jar","PCMaintanence.jar"]

ENV TZ=Asia/Bishkek



