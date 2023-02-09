#!/bin/bash

# Make predictions
for i in {100..1000..100}
do
    sed -i "4s#.*#PATH_RULES = data/rules.txt-$i#" config/config-SAFRAN
    sed -i "6s#.*#PATH_OUTPUT = data/predictions-max-$i#" config/config-SAFRAN
    cat config/config-SAFRAN
    ./SAFRAN/bin/SAFRAN applymax config/config-SAFRAN
done

