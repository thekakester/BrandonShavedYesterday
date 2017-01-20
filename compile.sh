#!/bin/bash
mkdir bin
javac -d bin src/game/*.java src/engine/*.java
java -cp bin game/Game