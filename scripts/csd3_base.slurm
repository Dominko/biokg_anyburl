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

. /etc/profile.d/modules.sh                # Leave this line (enables the module command)
module purge                               # Removes all modules still loaded
module load rhel8/default-icl              # REQUIRED - loads the basic environment
module load python-3.9.6-gcc-5.4.0-sbr552h

echo "Job running on ${SLURM_JOB_NODELIST}"
dt=$(date '+%d/%m/%Y %H:%M:%S')
echo "Job started: $dt"

echo "Loading CUDA"
module load cuda/10.2 cudnn/7.6_cuda-10.2

echo "Initialising env"
python3.9 -m venv ~/rds/hpc-work/anyburl_env

echo "source activate"
source ~/rds/hpc-work/anyburl_env/bin/activate

lscpu

java -Xmx12G -cp AnyBURL/AnyBURL-23-1.jar de.unima.ki.anyburl.Learn config/base/config-AnyBURL-learn-base

# Make predictions
for i in {50..300..50}
do
    sed -i "5s#.*#PATH_RULES = /rds/user/co-grab1/hpc-work/anyburl/rules-base-$i#" config/base/config-AnyBURL-apply
    sed -i "6s#.*#PATH_OUTPUT = /rds/user/co-grab1/hpc-work/anyburl/predictions-base-$i#" config/base/config-AnyBURL-apply
    
    java -Xmx12G -cp AnyBURL/AnyBURL-23-1.jar de.unima.ki.anyburl.Apply config/base/config-AnyBURL-apply
    python scripts/eval.py \
            /rds/user/co-grab1/hpc-work/anyburl/predictions-base-$i \
            /rds/user/co-grab1/hpc-work/anyburl/train_prefixed.csv \
            /rds/user/co-grab1/hpc-work/anyburl/valid_prefixed.csv \
            /rds/user/co-grab1/hpc-work/anyburl/test_prefixed.csv \
            /rds/user/co-grab1/hpc-work/anyburl/heads_base.pkl \
            /rds/user/co-grab1/hpc-work/anyburl/tails_base.pkl \
            /rds/user/co-grab1/hpc-work/anyburl/base.out \
            True
done
