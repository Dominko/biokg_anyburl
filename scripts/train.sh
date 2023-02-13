#!/bin/bash

# Recompile
cd AnyBURL/
#javac de/unima/ki/anyburl/*.java -d build
javac de/unima/ki/anyburl/*.java de/unima/ki/anyburl/algorithm/*.java de/unima/ki/anyburl/data/*.java de/unima/ki/anyburl/eval/*.java de/unima/ki/anyburl/exceptions/*.java de/unima/ki/anyburl/io/*.java de/unima/ki/anyburl/playground/*.java de/unima/ki/anyburl/structure/*.java de/unima/ki/anyburl/threads/*.java de/unima/ki/anyburl/structure/compare/*.java -d build
jar cfv AnyBURL-23-1.jar -C build .
cd ../

# Train model
java -Xmx12G -cp AnyBURL/AnyBURL-23-1.jar de.unima.ki.anyburl.Learn config/config-AnyBURL

# Make predictions
for i in {100..1000..100}
do
    sed -i "5s#.*#PATH_RULES = /rds/user/co-grab1/hpc-work/anyburl/rules-$i#" config/config-AnyBURL-apply
    sed -i "6s#.*#PATH_OUTPUT = /rds/user/co-grab1/hpc-work/anyburl/predictions-max-$i#" config/config-AnyBURL-apply
    sed -i "6s#.*#PATH_PREDICTIONS = /rds/user/co-grab1/hpc-work/anyburl/predictions-max-$i#" config/config-AnyBURL-eval
    
    java -Xmx12G -cp AnyBURL/AnyBURL-23-1.jar de.unima.ki.anyburl.Apply config/config-AnyBURL-apply
    java -Xmx12G -cp AnyBURL/AnyBURL-23-1.jar de.unima.ki.anyburl.Eval config/config-AnyBURL-eval
done

