#!/bin/bash

if [ $# -eq 0 ] 
then
	echo "Please use EVLib parent project as argument"
	exit
fi
# Rebuild the EVLib library
gradle build -p $1/EVLib
# Copy the EVLib module
cp -r $1/EVLib/ .
