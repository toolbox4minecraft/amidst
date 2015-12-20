#!/bin/bash

filename="`source scripts/get-amidst-filename.sh`"
cd "target/${filename}/"
zip -r "${filename}.zip" "AMIDST.app/"
mv "${filename}.zip" ".."

