#!/bin/bash
mvn install:install-file -Dfile=./lib/core-2.1.0-SNAPSHOT.jar -DgroupId=com.github.gumtreediff -DartifactId=core -Dpackaging=jar -Dversion=2.1.0-SNAPSHOT
mvn install:install-file -Dfile=./lib/gen.jdt-2.1.0-SNAPSHOT.jar -DgroupId=com.github.gumtreediff -DartifactId=gen.jdt -Dpackaging=jar -Dversion=2.1.0-SNAPSHOT