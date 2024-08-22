#!/bin/bash
START_TIME=$(($(date +'%s * 1000 + %-N / 1000000')))

java -DSTART_TIME=$START_TIME -Dspring.aot.enabled=true \
    -jar application/target/application-1.0.0.jar
