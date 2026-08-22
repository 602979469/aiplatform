# 构建产物：bootstrap/target/aiplatform-bootstrap-*.jar
FROM docker.xuanyuan.run/library/eclipse-temurin:17-jre

WORKDIR /app

COPY bootstrap/target/aiplatform-bootstrap-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "-Duser.timezone=Asia/Shanghai", \
    "-Xms256m", \
    "-Xmx512m", \
    "-XX:MetaspaceSize=128m", \
    "-XX:MaxMetaspaceSize=256m", \
    "-jar", \
    "app.jar"]
