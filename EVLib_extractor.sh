#!/bin/bash

gradle build -p $1/EVLib

cp -r $1/EVLib/ .
