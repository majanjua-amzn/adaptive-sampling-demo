#!/bin/bash

# Check if argument is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <number_of_calls>"
    echo "Example: $0 10"
    exit 1
fi

# Check if argument is a positive integer
if ! [[ "$1" =~ ^[0-9]+$ ]] || [ "$1" -eq 0 ]; then
    echo "Error: Argument must be a positive integer"
    exit 1
fi

NUM_CALLS=$1
URL="http://localhost:8080/status/c/200"

echo "Making $NUM_CALLS calls to $URL"

for i in $(seq 1 $NUM_CALLS); do
    curl -s "$URL" > /dev/null
done

curl -s "http://localhost:8080/status/c/500" > /dev/null

echo "Completed $NUM_CALLS calls (plus 1 failure call)"
