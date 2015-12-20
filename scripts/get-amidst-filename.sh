#!/bin/bash

grep amidst.filename src/main/resources/amidst/metadata.properties | cut -d "=" -f 2
