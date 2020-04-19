#!/usr/bin/env bash

echo "The application will start in ${APP_SLEEP}s..."
sleep "${APP_SLEEP}"
exec /app.jar run "$@"
