#!/bin/bash

if [ $# -eq 0 ]
then
	echo "Please use sample code parent project as argument"
	exit
fi

target="$@/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/sample/"
dest="sample/"

if [ ! -d "$target" ]
then
	echo "'$target' does not exist or is not a directory."
	exit
fi

if [ -d "$dest" ]
then
	echo "Deleting old $dest directory"
	rm -r "$dest"
fi
echo "copying '$target' to '$dest'"
cp -r "$target" "$dest"
# Copy the sample code package to this directory

