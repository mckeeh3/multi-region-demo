#!/bin/bash

# Check if hostname parameter is provided
if [ $# -eq 2 ]; then
    userId="$2"
    host="https://$1/user/$userId"
elif [ $# -eq 1 ]; then
    userId="$1"
    host="http://localhost:9000/user/$userId"
else
    echo "Usage: $0 [hostname] <userId>"
    exit 1
fi

curl "${host}"
