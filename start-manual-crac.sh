#!/bin/bash

# Enable checkpoint compression (will reduce restore time a bit)
export CRAC_CRIU_OPTS=--compress

rm -fR tmp_manual_checkpoint/*
START_TIME=$(($(date +'%s * 1000 + %-N / 1000000')))
java -DSTART_TIME=$START_TIME -XX:CRaCCheckpointTo=./tmp_manual_checkpoint -jar application/target/*.jar