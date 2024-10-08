#!/bin/bash

# Check if hostname parameter is provided
if [ $# -eq 3 ]; then
    url="https://$1/user/change-email"
    userId="$2"
    email="$3"
elif [ $# -eq 2 ]; then
    url="http://localhost:9000/user/change-email"
    userId="$1"
    email="$2"
else
    echo "Usage: $0 [hostname] <userId> <email>"
    exit 1
fi

# Execute the curl command
curl -X PUT "${url}" \
-H 'Content-Type: application/json' \
-d '{
    "userId": "'"$userId"'",
    "email": "'"$email"'"
}'

# Add a newline for better readability of output
echo
