#!/bin/bash

# Check if hostname parameter is provided
if [ $# -eq 2 ]; then
    userId="$2"
    url="https://$1/user/$userId"
elif [ $# -eq 1 ]; then
    userId="$1"
    url="http://localhost:9000/user/$userId"
else
    echo "Usage: $0 [hostname] <userId>"
    exit 1
fi

# Execute the curl command
curl -s \
    -H "Accept: application/json" \
    "${url}"

# Add a newline for better readability of output
echo
