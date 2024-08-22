#!/bin/bash
START_TIME=$(($(date +'%s * 1000 + %-N / 1000000')))

java -DSTART_TIME=$START_TIME -Dspring.aot.enabled=true \
    -agentlib:native-image-agent=config-output-dir=/tmp/live-kitchen-native-image/,config-write-period-secs=300,config-write-initial-delay-secs=5 \
    -jar application/target/application-1.0.0.jar
