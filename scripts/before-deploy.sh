#!/bin/bash

filename="`source scripts/get-amidst-filename.sh`"
rm "target/${filename}.jar"
mv "target/${filename}-jar-with-dependencies.jar" "target/${filename}.jar"

