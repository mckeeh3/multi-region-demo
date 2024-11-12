#!/bin/bash

# Check if hostname parameter is provided
if [ $# -eq 4 ]; then
    host="https://$1/user"
    userId="$2"
    name="$3"
    email="$4"
elif [ $# -eq 3 ]; then
    host="http://localhost:9000/user"
    userId="$1"
    name="$2"
    email="$3"
else
    echo "Usage: $0 [hostname] <userId> <name> <email>"
    echo "Create a new user with the given userId, name, and email"
    exit 1
fi

curl -sX POST "${host}" \
-H 'Content-Type: application/json' \
-d '{
        "userId": "'"$userId"'",
        "name": "'"$name"'",
        "email": "'"$email"'"
    }'
