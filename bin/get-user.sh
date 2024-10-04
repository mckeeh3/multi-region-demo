#!/bin/bash

# Default hostname
hostname="localhost:9000"

# Check if hostname parameter is provided
if [ $# -eq 2 ]; then
    hostname="$1"
    userId="$2"
elif [ $# -eq 1 ]; then
    userId="$1"
else
    echo "Usage: $0 [hostname] <userId>"
    exit 1
fi

# Execute the curl command
curl -X GET "http://$hostname/user/$userId" \
-v \
-H 'Accept: application/json'

# Add a newline for better readability of output
echo
