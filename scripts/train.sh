#!/bin/bash

# Recompile
cd AnyBURL/
javac de/unima/ki/anyburl/*.java -d build
jar cfv AnyBURL-23-1.jar -C build .
cd ../

# Train model
java -Xmx12G -cp AnyBURL/AnyBURL-23-1.jar de.unima.ki.anyburl.Learn config/config-AnyBURL

# Make predictions
for i in {100..1000..100}
do
    sed -i "5s#.*#PATH_RULES = data/rules.txt-$i#" config/config-AnyBURL-apply
    sed -i "6s#.*#PATH_OUTPUT = data/predictions-max-$i#" config/config-AnyBURL-apply
    sed -i "6s#.*#PATH_PREDICTIONS = data/predictions-max-$i#" config/config-AnyBURL-eval
    
    java -Xmx12G -cp AnyBURL/AnyBURL-23-1.jar de.unima.ki.anyburl.Apply config/config-AnyBURL-apply
    java -Xmx12G -cp AnyBURL/AnyBURL-23-1.jar de.unima.ki.anyburl.Eval config/config-AnyBURL-eval
done

