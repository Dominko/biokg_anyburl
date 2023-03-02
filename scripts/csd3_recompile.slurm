#!/bin/bash
# # SBATCH -o /home/%u/slogs/sl_%A.out
# # SBATCH -e /home/%u/slogs/sl_%A.out
#SBATCH -N 1                            # nodes requested
#SBATCH -n 1                            # tasks requested
#SBATCH --mem=20000                     # memory in Mb
#SBATCH --partition=icelake
#SBATCH --account=BMAI-CDT-SL2-CPU
#SBATCH -t 24:00:00                     # time requested in hour:minute:seconds
#SBATCH --exclusive

echo "Job running on ${SLURM_JOB_NODELIST}"
dt=$(date '+%d/%m/%Y %H:%M:%S')
echo "Job started: $dt"

# Load required modules
module load openjdk-11.0.2-gcc-5.4.0-3dxltae

lscpu

# Recompile
cd AnyBURL/
#javac de/unima/ki/anyburl/*.java -d build
javac de/unima/ki/anyburl/*.java de/unima/ki/anyburl/algorithm/*.java de/unima/ki/anyburl/data/*.java de/unima/ki/anyburl/eval/*.java de/unima/ki/anyburl/exceptions/*.java de/unima/ki/anyburl/io/*.java de/unima/ki/anyburl/playground/*.java de/unima/ki/anyburl/structure/*.java de/unima/ki/anyburl/threads/*.java de/unima/ki/anyburl/structure/compare/*.java -d build
jar cfv AnyBURL-23-1.jar -C build .
cd ../
