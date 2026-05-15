#!/usr/bin/env bash

set -e

sourcefile="amanda-swanepoel.jpeg"
sourcename="${sourcefile%%.*}"

echo "Sourcing $sourcefile with local name $sourcename"

echo "==="
echo "Remove existing files"
for file in "./${sourcename}"*; do
    [[ "$( basename "$file" )" != "amanda-swanepoel.jpeg" ]] && rm -v "$file"
done

echo "==="
echo "Start generation"


echo "Smaller JPEG"
npx sharp -i "$sourcefile" -o "$sourcename-small.jpeg" --format jpeg resize 400

echo "Meta-rotated JPEG (2x)"
npx sharp -i "$sourcefile" -o "$sourcename-exif-rotate-3.jpeg" --format jpeg --metadata.orientation 3 resize 600
npx sharp -i "$sourcefile" -o "$sourcename-exif-rotate-6.jpeg" --format jpeg --metadata.orientation 6 resize 600

echo "Image as WEBP"
npx sharp -i "$sourcefile" -o "$sourcename.webp" --metadata --format webp

echo "Smaller as WEBP"
npx sharp -i "$sourcefile" -o "$sourcename-small.webp" --metadata --format webp resize 400

echo "Meta-rotated WEBP (2x)"
npx sharp -i "$sourcefile" -o "$sourcename-exif-rotate-3.webp" --metadata --format webp --metadata.orientation 3 resize 600
npx sharp -i "$sourcefile" -o "$sourcename-exif-rotate-6.webp" --metadata --format webp --metadata.orientation 6 resize 600

echo "Image as AVIF"
npx sharp -i "$sourcefile" -o "$sourcename.avif" --metadata --format avif

echo "==="
echo "File mime scan"

for file in "./${sourcename}"*; do
    mimetype "$file"
done
