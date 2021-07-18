#!make

.PHONY: install fmt clean updateVersion

install: fmt
	@ mvn install

fmt:
	@ mvn spotless:apply

clean:
	@ mvn clean

updateVersion:
	@ mvn versions:set -DnewVersion=1.0.0-SNAPSHOT
	@ mvn versions:commit
