#!/bin/bash

# Default hostname
hostname="localhost:9000"

# Check if hostname parameter is provided
if [ $# -eq 3 ]; then
    hostname="$1"
    userId="$2"
    email="$3"
elif [ $# -eq 2 ]; then
    userId="$1"
    email="$2"
else
    echo "Usage: $0 [hostname] <userId> <email>"
    exit 1
fi

# Execute the curl command
curl -X PUT "http://$hostname/user/change-email" \
-v \
-H 'Content-Type: application/json' \
-d '{
  "userId": "'"$userId"'",
  "email": "'"$email"'"
}'

# Add a newline for better readability of output
echo
