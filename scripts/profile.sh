#!/usr/bin/env bash

# 쉬고 있는 profile 찾기: real1이 사용 중이면 real2가 쉬고 있고, 반대면 real1이 쉬고 있음
function find_idle_profile()
{
    # 현재 nginx가 바라보고 있는 스프링 부트가 정상적으로 수행중인지 확인한다. (by. http status code)
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile)

    if [ ${RESPONSE_CODE} -ge 400 ]      # 400보다 크면 (http status code가 400, 500대인 경우)
    then
        CURRENT_PROFILE=real
    else
        CURRENT_PROFILE=$(curl -s http://localhost/profile)
    fi

    if [ "$CURRENT_PROFILE" == dev ]
    then
        IDLE_PROFILE=real
    else
        IDLE_PROFILE=dev
    fi

    echo "${IDLE_PROFILE}"
}

# 쉬고 있는 profile의 port 찾기
function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ "$IDLE_PROFILE" == dev]
    then
        echo "8081"
    else
        echo "8082"
    fi
}