#!/bin/bash

filename="`source travis-ci/scripts/get-amidst-filename.sh`"
cd "travis-ci/wrapper-for-mac/target/${filename}/"
rm "${filename}.zip"
zip -r "${filename}.zip" "AMIDST.app/"
mv "${filename}.zip" "../../../../target/"
