# !/usr/bin/env bash
# step2 의 deploy.sh 와 유사
# deploy.sh 와의 차이점 : IDLE_PROFILE을 통해 properties 파일을 가져오고 active profile을 지정한다.
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

here = $(pwd)
echo "----------현재 위치---------- $here"

REPOSITORY=/home/ec2-user/app/nonstop
PROJECT_NAME=AhChacha-Backend

echo ">> Build 파일 복사"
echo ">> cp $REPOSITORY/zip/*.jar $REPOSITORY/"

cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo ">> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo ">> JAR_NAME: $JAR_NAME"

echo ">> $JAR_NAME 에 실행권한 추가"
chmod +x $JAR_NAME

echo ">> $JAR_NAME 실행"
IDLE_PROFILE=$(find_idle_profile)

echo ">> $JAR_NAME 를 profile=$IDLE_PROJECT 로 실행합니다."
nohup java -jar \
        -Dspring.config.location=classpath:$REPOSITORY/zip/application.yml \
        -Dspring.profiles.active=$IDLE_PROFILE \
        $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &