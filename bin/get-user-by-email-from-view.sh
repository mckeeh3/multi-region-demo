#!/bin/bash

# Check if hostname parameter is provided
if [ $# -eq 2 ]; then
    email="$2"
    url="https://$1/user-view/by-id/$email"
elif [ $# -eq 1 ]; then
    email="$1"
    url="http://localhost:9000/user-view/by-email/$email"
else
    echo "Usage: $0 [hostname] <email>"
    exit 1
fi

# Execute the curl command
curl -s \
    -H "Accept: application/json" \
    "${url}"

# Add a newline for better readability of output
echo