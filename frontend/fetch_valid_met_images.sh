#!/bin/bash

# Base URLs
BASE_URL="https://collectionapi.metmuseum.org/public/collection/v1/objects"
DETAIL_URL="https://collectionapi.metmuseum.org/public/collection/v1/objects"

# Number of valid images to fetch
LIMIT=10
COUNT=0

# Check if 'jq' is installed
if ! command -v jq &> /dev/null; then
    echo "jq is not installed. Please install jq to run this script."
    exit 1
fi

echo "Fetching Object IDs from the Met API..."
OBJECT_IDS=$(curl -s "$BASE_URL" | jq -r '.objectIDs[]')

if [ -z "$OBJECT_IDS" ]; then
    echo "Failed to fetch Object IDs. Exiting."
    exit 1
fi

echo "Scanning for valid images..."
for ID in $OBJECT_IDS; do
    # Fetch object details
    DETAILS=$(curl -s "$DETAIL_URL/$ID")
    IMAGE_URL=$(echo "$DETAILS" | jq -r '.primaryImage')

    # Check if primaryImage exists and is not empty
    if [ -n "$IMAGE_URL" ] && [ "$IMAGE_URL" != "null" ]; then
        TITLE=$(echo "$DETAILS" | jq -r '.title')
        ARTIST=$(echo "$DETAILS" | jq -r '.artistDisplayName')
        echo "Found: ID=$ID, Title=\"$TITLE\", Artist=\"$ARTIST\", Image=$IMAGE_URL"
        COUNT=$((COUNT+1))
    fi

    # Stop when we reach the limit
    if [ "$COUNT" -ge "$LIMIT" ]; then
        break
    fi
done

echo "Finished. Found $COUNT objects with valid images."
