#!make

.PHONY: install test verify compile fmt clean updateVersion

install: fmt
	@ mvn install
	@ docker build . -t iam

test: fmt
	@ mvn test

verify: fmt
	@ mvn verify
	@ docker build . -t iam

compile: fmt
	@ mvn compile

fmt:
	@ mvn spotless:apply

clean:
	@ mvn clean

updateVersion:
	@ mvn versions:set -DnewVersion=1.0.0-SNAPSHOT
	@ mvn versions:commit
