#!/bin/bash
# # SBATCH -o /home/%u/slogs/sl_%A.out
# # SBATCH -e /home/%u/slogs/sl_%A.out
#SBATCH -N 1                            # nodes requested
#SBATCH -n 1                            # tasks requested
#SBATCH --mem=20000                     # memory in Mb
#SBATCH --partition=icelake
#SBATCH --account=BMAI-CDT-SL2-CPU
#SBATCH -t 24:00:00                     # time requested in hour:minute:seconds

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
python3.9 -m pip install tqdm
python3.9 -m pip install pandas
python3.9 -m pip install numpy

# Create new env


# Activate 


echo "Running evaluate.sh"
#bash scripts/evaluate.sh
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-10 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-20 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-30 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-40 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-50 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-60 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-70 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-80 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-90 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
python scripts/eval.py /rds/user/co-grab1/hpc-work/anyburl/predictions-max-100 /rds/user/co-grab1/hpc-work/anyburl/train.csv /rds/user/co-grab1/hpc-work/anyburl/valid.csv /rds/user/co-grab1/hpc-work/anyburl/test.csv /rds/user/co-grab1/hpc-work/anyburl/heads.pkl /rds/user/co-grab1/hpc-work/anyburl/tails.pkl True
