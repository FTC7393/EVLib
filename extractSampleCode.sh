#!/bin/bash

if [ $# -eq 0 ]
then
	echo "Please use sample code parent project as argument"
	exit
fi

target="$@/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/sample/"

if [ ! -d "$target" ]
then
	echo "'$target' does not exist or is not a directory."
	exit
fi

rm -r sample
cp -r "$target" .
# Copy the sample code package to this directory

