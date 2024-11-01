# jdk17 Image Start
FROM openjdk:17

# 인자 설정 - JAR_File
ARG JAR_FILE=build/libs/*.jar

# 환경 변수 설정
ARG DB_URL
ARG DB_PWD
ARG GOOGLE_CLIENT_ID
ARG GOOGLE_CLIENT_SECRET

# jar 파일 복제
COPY ${JAR_FILE} app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

# 타임존 아시아 서울 적용
ENV TZ=Asia/Seoul

# 환경 변수 설정
ENV DB_URL=$DB_URL
ENV DB_PWD=$DB_PWD
ENV GOOGLE_CLIENT_ID=$GOOGLE_CLIENT_ID
ENV GOOGLE_CLIENT_SECRET=$GOOGLE_CLIENT_SECRET
