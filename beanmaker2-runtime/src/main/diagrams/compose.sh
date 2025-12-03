#!/usr/bin/env bash

extensions=(".svg" ".png" ".pdf")

for file in *.d2; do
  if [ -f "$file" ]; then
    for ext in "${extensions[@]}"; do
      output_file="${file%.d2}${ext}"
      d2 "$file" "$output_file"
    done
  fi
done
