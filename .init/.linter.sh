#!/bin/bash
cd /home/kavia/workspace/code-generation/premium-android-tv-fitness-hub-40918-40927/tv_app_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

