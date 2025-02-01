#!/usr/bin/env bash

# Consistent root
echo "Move to script dir"
cd "$( dirname "$( realpath "${BASH_SOURCE[0]}" )" )" || exit 1

PLANT_PORT=5500

# Fire up a plantuml docker image
if [ "$( docker container inspect plantuml --format '{{ .State.Status }}' 2>/dev/null )" != "running" ]; then
    echo "Removing any existing PlantUML container"
    docker rm --force plantuml 2>/dev/null || true

    echo "Starting new PlantUML container"
    docker run \
        --detach \
        --publish 127.0.0.1:$PLANT_PORT:8080 \
        --rm \
        --name plantuml \
        plantuml/plantuml-server:jetty

    while ! curl --silent --fail http://localhost:$PLANT_PORT > /dev/null; do
        echo "Waiting for container to be ready..."
        sleep 1
    done;
fi

# List files
for file in ./*.puml; do
    fullpath="$( realpath "$file" )"
    dirpath="$( dirname "$fullpath" )"
    filename=$( basename "$fullpath" )
    newFilename="${filename%.*}.svg"

echo "Processing ${filename} to ${newFilename}"
    curl -o "$dirpath/$newFilename" --data-binary "@${fullpath}" http://localhost:$PLANT_PORT/svg
done
