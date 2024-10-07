#!/bin/bash

# Default hostname
hostname="localhost:9000"

# Check if at least one parameter is provided
if [ $# -eq 2 ]; then
    url="https://$1/user-view/by-id/$2"
    userId="$2"
elif [ $# -eq 1 ]; then
    url="http://localhost:9000/user-view/by-id/$1"
    userId="$1"
else
    echo "Usage: $0 [hostname] <userId>"
    exit 1
fi

# Execute the curl command
curl -v \
    -H "Accept: application/json" \
    "${url}"

# Add a newline for better readability of output
echo
