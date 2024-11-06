#!/bin/bash

# Check if hostname parameter is provided
if [ $# -eq 1 ]; then
    url="https://$1/user-view/all"
else
    url="http://localhost:9000/user-view/all"
fi

curl "${url}"
