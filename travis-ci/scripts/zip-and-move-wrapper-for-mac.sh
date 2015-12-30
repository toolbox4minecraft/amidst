#!/bin/bash

filename="`source travis-ci/scripts/get-amidst-filename.sh`"
cd "travis-ci/wrapper-for-mac/target/${filename}/"
zip -r "${filename}.zip" "Amidst.app/"
mv "${filename}.zip" "../../../../target/"
