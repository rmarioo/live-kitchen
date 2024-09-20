#!/bin/bash

# Check if PID is provided
if [ -z "$1" ]; then
  echo "Error: PID is a mandatory parameter."
  echo "Usage: $0 <PID>"
  exit 1
fi

# Store the provided PID
PID=$1

# Check if the PID is valid and the process exists
if ! ps -p "$PID" > /dev/null 2>&1; then
  echo "Error: Process with PID $PID does not exist."
  exit 1
fi

# Get the memory usage in MB and display it
MEMORY=$(ps -p "$PID" -o rss | awk 'NR>1 {print $1/1024 " MB"}')
echo "Memory usage of process with PID $PID: $MEMORY"

