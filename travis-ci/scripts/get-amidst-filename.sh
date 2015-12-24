#!/bin/bash

grep amidst.build.filename src/main/resources/amidst/metadata.properties | cut -d "=" -f 2
