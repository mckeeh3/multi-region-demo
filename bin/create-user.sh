#!/bin/bash

# Default hostname
hostname="localhost:9000"

# Check if hostname parameter is provided
if [ $# -eq 4 ]; then
    hostname="$1"
    userId="$2"
    name="$3"
    email="$4"
elif [ $# -eq 3 ]; then
    userId="$1"
    name="$2"
    email="$3"
else
    echo "Usage: $0 [hostname] <userId> <name> <email>"
    exit 1
fi

# Execute the curl command
curl -X POST "http://$hostname/user" \
-v \
-H 'Content-Type: application/json' \
-d '{
  "userId": "'"$userId"'",
  "name": "'"$name"'",
  "email": "'"$email"'"
}'

# Add a newline for better readability of output
echo
