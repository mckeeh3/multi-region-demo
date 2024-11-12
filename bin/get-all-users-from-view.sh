#!/bin/bash

# Check if hostname parameter is provided
if [ $# -eq 1 ]; then
    host="https://$1/user-view/all"
else
    host="http://localhost:9000/user-view/all"
fi

curl "${host}"
