#!/usr/bin/env bash
# ------------------------------------------------------------------------------
# run_all.sh
#
# Usage:
#   open_project.sh start
# or simply
#   ./open_project.sh
#
# This will:
#  1) Launch Spring Boot in the backend folder in the background
#  2) Launch ng serve --open in the frontend folder
#  3) Wait until you Ctrl+C to stop them
#
# Optionally:
#   ./open_project.sh stop
# can kill any leftover processes if needed.
# ------------------------------------------------------------------------------

BACKEND_DIR="backend"
FRONTEND_DIR="frontend"

SPRING_PID=""
ANGULAR_PID=""

start_services() {
  echo "=== Starting Spring Boot (backend) in background... ==="
  pushd "$BACKEND_DIR" >/dev/null 2>&1
  # Run Spring Boot in background
  ./mvnw spring-boot:run &
  SPRING_PID=$!
  echo "Spring Boot PID: $SPRING_PID"
  popd >/dev/null 2>&1

  # Give Spring Boot a moment to start
  sleep 5

  echo "=== Starting Angular dev server (frontend)... ==="
  pushd "$FRONTEND_DIR" >/dev/null 2>&1
  # Run ng serve in foreground (so we can see logs)
  ng serve --open
  # As soon as the ng serve ends, we proceed
  popd >/dev/null 2>&1
}

stop_services() {
  echo "=== Stopping services... ==="
  if [ -n "$SPRING_PID" ]; then
    echo "Killing Spring Boot with PID $SPRING_PID..."
    kill "$SPRING_PID" 2>/dev/null || true
  fi

  # If you want to also kill ng serve, you'd need to run it in background
  # and store ANGULAR_PID. But here, by default, ng serve is foreground.
  # If you did want ng in background, see note below.
  echo "All services stopped."
}

# If you prefer to run Angular in background as well, you can do:
#    (ng serve --open) &
#    ANGULAR_PID=$!
# and then kill it in stop_services.

# Main logic
CMD=${1:-start}

case "$CMD" in
  start)
    start_services
    ;;
  stop)
    stop_services
    ;;
  *)
    echo "Usage: $0 [start|stop]"
    exit 1
    ;;
esac
