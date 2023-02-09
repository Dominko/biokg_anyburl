#!/bin/bash

cd AnyBURL/
javac de/unima/ki/anyburl/*.java -d build
jar cfv AnyBURL-23-1.jar -C build .
cd ../