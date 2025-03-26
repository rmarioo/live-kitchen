#!/bin/bash

START_TIME=$(($(date +'%s * 1000 + %-N / 1000000')))
java -DSTART_TIME=$START_TIME -XX:CRaCRestoreFrom=./tmp_manual_checkpoint
