#!/bin/bash

START_TIME=$(($(date +'%s * 1000 + %-N / 1000000')))
java -DSTART_TIME=$START_TIME -jar application/target/*.jar --server.port=8888
