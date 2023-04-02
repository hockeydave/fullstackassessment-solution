#!/bin/bash

pushd todo-client || exit 1

concurrently --prefix-colors "bgGreenBright.bold,bgBlueBright.bold" \
                  --kill-others \
                  --names "Todo Server,Todo Client" \
  "../todo-server/gradlew --project-dir ../todo-server/ bootRun" \
  "npm start"

popd || exit 1
