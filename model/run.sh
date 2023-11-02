#!/bin/bash

BASEDIR=$(pwd)
LAG=4
MA=0
TEST_SET_RATIO=0.5
MODEL=arima

DIR="${BASEDIR}/log/${MODEL}-test_set_ratio_${TEST_SET_RATIO}-MA_${MA}"
FILE="${DIR}/info.log"

if [[ ! -e $DIR ]]; then
    echo $DIR
    mkdir -p $DIR
fi

CMD="python src/main.py
    --base-path ${BASEDIR}
    --lag ${LAG}
    --ma ${MA}
    --test-set-ratio ${TEST_SET_RATIO}
    --model ${MODEL}
    | tee ${FILE}
    "

echo ${CMD}
eval ${CMD}
