#!/bin/bash

pushd todo-client || exit 1

concurrently yarn --prefix-colors "bgGreenBright.bold,bgBlueBright.bold" \
                  --kill-others \
                  --names "Todo Server,Todo Client" \
  "../todo-server/gradlew --project-dir ../todo-server/ bootRun" \
  "yarn start"

popd || exit 1
