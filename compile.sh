#!/bin/bash
mkdir bin
javac -d bin src/game/*.java src/engine/*.java src/entity/*.java
java -cp bin game/Game