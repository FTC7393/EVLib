#!/bin/bash

if [ $# -eq 0 ]
then
	echo "Please use EVLib parent project as argument"
	exit
fi

target="$@/EVLib"

if [ ! -d "$target" ]
then
	echo "'$target' does not exist or is not a directory."
	exit
fi

# Rebuild the EVLib library
gradle build -p "$target" && \
cp -r "$target" . && \
cp "$target/build/outputs/aar/EVLib-release.aar" .
# Copy the EVLib module and .aar file to this directory
