#!make

.PHONY: build fmt clean

build:
	@ mvn verify

fmt:
	@ mvn spotless:apply

clean:
	@ mvn clean
