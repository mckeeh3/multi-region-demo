#!/bin/bash

# Check if hostname parameter is provided
if [ $# -eq 4 ]; then
    url="https://$1/user"
    userId="$2"
    name="$3"
    email="$4"
elif [ $# -eq 3 ]; then
    url="http://localhost:9000/user"
    userId="$1"
    name="$2"
    email="$3"
else
    echo "Usage: $0 [hostname] <userId> <name> <email>"
    exit 1
fi

# Execute the curl command
curl -X POST "${url}" \
-H 'Content-Type: application/json' \
-d '{
  "userId": "'"$userId"'",
  "name": "'"$name"'",
  "email": "'"$email"'"
}'

# Add a newline for better readability of output
echo
