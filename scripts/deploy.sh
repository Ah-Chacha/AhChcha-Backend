# !/bin/bash
REPOSITORY=/home/ec2-user/app/step2
PROJECT_NAME=AhChacha-Backend

echo ">> Build 파일 복사"
cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo ">> 현재 구동 중인 애플리케이션 pid 확인"
# CURRENT_PID=$(pgrep -f $PROJECT_NAME)
CURRENT_PID=$(pgrep -fl $PROJECT_NAME | grep jar | awk '{print $1}')
echo "   현재 구동 중인 애플리케이션 pid : $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo ">> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo ">> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo ">> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
echo ">> JAR Name : $JAR_NAME"

echo ">> $JAR_NAME 에 실행 권한 추가"
chmod +x $JAR_NAME

echo ">> $JAR_NAME 실행 "
nohup java -jar \
        -Dspring.config.location=file:///home/ec2-user/app/step2/zip/application.yml \
        $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

#         -Dspring.config.location=classpath:/application.yml \
#         -Dspring.profiles.active=real \
