Building Amidst
=====

Amidst uses maven for its build process. Here are the steps to build from source:

* `mvn clean`
* `mvn install`
 
This will place the jar file under `target/AMIDST-<version>.jar`.

To build the wrapper for mac, follow these steps:

* `mvn clean`
* `mvn install`
* `bash travis-ci/scripts/create-mac-icon.sh`
* `mvn package -DskipTests=true -f travis-ci/wrapper-for-mac/pom.xml`
* `bash travis-ci/scripts/zip-and-move-wrapper-for-mac.sh`

This will place the zip file under `target/AMIDST-<version>.zip`.

To build the wrapper for windows, follow these steps:

* `mvn clean`
* `mvn install`
* `bash travis-ci/scripts/create-windows-icon.sh`
* `mvn package -DskipTests=true -f travis-ci/wrapper-for-windows/pom.xml`

This will place the exe file under `target/AMIDST-<version>.exe`.

You will need imagemagick installed to create the icons. Of course, you can also create the icon files by yourself and place them as `target/icon.icns` (mac) or  `target/icon.ico` (windows) to the expected location. The `zip-and-move-wrapper-for-mac.sh` bash script simply creates a zip file from the directory located at `travis-ci/wrapper-for-mac/target/AMIDST-<version>/`. You can also do this by yourself.

All of these steps are also executed by travis-ci to create a new release. However the regular travis-ci build will not create the wrappers for mac and windows.

The command `mvn clean` will actually install a dependency that is not available from a public maven repository to the local maven repository, so it is necessary to execute.