# JHTTP

## Introduction

JHTTP is a simple HTTP server written in the Java 8.

## Requirements

* Java 8 SDK
* Maven

## Building

    mvn clean package

It creates a self-contained, executable JAR in the `target` directory.

## Run

    java -jar jhttp*.jar [server-root]

Runs the server on 8888 port. Optional parameter points to the server root
directory. If it's absent, then the current directory will be used.