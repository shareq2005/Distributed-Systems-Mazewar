#!/bin/bash
JAVA_HOME=/cad2/ece419s/java/jdk1.6.0/

# $1 - Hostname of the server
# $2 - Port Number the server is running on

${JAVA_HOME}/bin/java Mazewar $1 $2
