#!/bin/bash

# Check if the correct number of parameters is provided
if [ $# -eq 2 ]; then
    host="https://$1/user"
    userId="$2"
elif [ $# -eq 1 ]; then
    host="http://localhost:9000/user"
    userId="$1"
else
    echo "Usage: $0 [hostname] <userId>"
    exit 1
fi

name="$userId"
email="${userId}@gmail.com"

curl -sX POST "${host}" \
-H 'Content-Type: application/json' \
-d '{
        "userId": "'"$userId"'",
        "name": "'"$name"'",
        "email": "'"$email"'"
    }'
