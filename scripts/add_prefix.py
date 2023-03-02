import pandas as pd
import sys

def add_prefix(path_in, path_out):
    file = pd.read_csv(path_in, sep="\t", header=None)
    for col in file.columns:
        file[col] = 'a' + file[col].astype(str)
    file.to_csv(path_out, sep="\t", header=None, index=False)

if __name__ == "__main__":
    add_prefix(sys.argv[1], sys.argv[2])