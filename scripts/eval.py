import os
import math
from tqdm import tqdm
import sys

import pandas as pd
from io import StringIO

import pickle

from threading import Thread, Lock
from queue import Queue

from time import time

def parse_preds(path_predictions):
    with open(path_predictions) as prediction_file:
        lines = prediction_file.readlines()

        # Sanity check if everything is correct
        assert len(lines) % 3 == 0

        all_heads = pd.DataFrame(columns=["triple", "prediction", "confidence"])
        all_tails = pd.DataFrame(columns=["triple", "prediction", "confidence"])

        heads = []
        tails = []

        test = 0
        for i in tqdm(range(test, len(lines)//3)):
            triple = lines[i*3].strip()

            # Extract heads
            heads.append(lines[i*3 + 1].split("Heads: ")[1].strip())
            if not heads[i-test] == "":
                heads[i-test] = StringIO(heads[i-test])
                heads[i-test] = pd.read_csv(heads[i-test], sep="\t", header=None).to_numpy()
                heads[i-test] = pd.DataFrame(heads[i-test].reshape((len(heads[i-test][0])//2,2)), columns=["prediction", "confidence"])
                heads[i-test]["prediction"] = heads[i-test]["prediction"].astype(int).astype(str) + " " + triple.split(" ")[1] + " " + triple.split(" ")[2]
                heads[i-test]["triple"] = triple
                # heads.set_index("triple", inplace = True)
            else:
                heads[i-test] = pd.DataFrame(columns=["triple", "prediction", "confidence"])
                # heads.set_index("triple", inplace = True)
                # TODO: If missing, 0, add to log

            # Extract tails
            tails.append(lines[i*3 + 2].split("Tails: ")[1].strip())
            if not tails[i-test] == "":
                tails[i-test] = StringIO(tails[i-test])
                tails[i-test] = pd.read_csv(tails[i-test], sep="\t", header=None).to_numpy()
                tails[i-test] = pd.DataFrame(tails[i-test].reshape((len(tails[i-test][0])//2,2)), columns=["prediction", "confidence"])
                tails[i-test]["prediction"] = triple.split(" ")[0] + " " + triple.split(" ")[1] + " " + tails[i-test]["prediction"].astype(int).astype(str)
                tails[i-test]["triple"] = triple
                # tails.set_index("triple", inplace = True)
            else:
                tails[i-test] = pd.DataFrame(columns=["triple", "prediction", "confidence"])
                # tails.set_index("triple", inplace = True)
                # TODO: If missing, 0, add to log
            

            
            # print(triple)
            # print(heads)
            # print(tails)

            # if i == 500:
            #     break
        all_heads = pd.concat(heads)
        all_tails = pd.concat(tails)

        # print(all_heads)
        # print(all_tails)
        return all_heads, all_tails


def evaluate(path_predictions, path_train, path_valid, path_test, head_path, tail_path, output_path, force_regen:bool):
    # Read prediction file and extract predictions as dict
    if not os.path.isfile(head_path) or not os.path.isfile(tail_path):
        print("we are missing parsed preds, generate...")
        heads, tails = parse_preds(path_predictions)
        with open(head_path, 'wb') as f:
            pickle.dump(heads, f)
        with open(tail_path, 'wb') as f:
            pickle.dump(tails, f)
    elif force_regen:
        print("Regeneration requested...")
        heads, tails = parse_preds(path_predictions)
        with open(head_path, 'wb') as f:
            pickle.dump(heads, f)
        with open(tail_path, 'wb') as f:
            pickle.dump(tails, f)
    else:
        print("Reading heads and tails from file...")
        with open(head_path, 'rb') as f:
            heads = pickle.load(f)
        with open(tail_path, 'rb') as f:
            tails = pickle.load(f)

    # Read raw data files
    print("Loading dataset")
    train = pd.read_csv(path_train, sep="\t",header=None)
    valid = pd.read_csv(path_valid, sep="\t",header=None)
    test = pd.read_csv(path_test, sep="\t",header=None)

    print("Combining dataset")
    all_triples = pd.concat([train, test])
    all_triples["triple"] = all_triples[0].astype(str) + " " + all_triples[1].astype(str) + " " + all_triples[2].astype(str)
    all_triples = all_triples["triple"]

    print("Filtering")
    heads = heads.merge(all_triples, left_on="prediction", right_on="triple", indicator=True, how="outer").query('_merge=="left_only"|triple_x==prediction').drop(['_merge', 'triple_y'], axis=1)
    tails = tails.merge(all_triples, left_on="prediction", right_on="triple", indicator=True, how="outer").query('_merge=="left_only"|triple_x==prediction').drop(['_merge', 'triple_y'], axis=1)

    print(heads)

    # heads.sort_values("triple_x", inplace = True)
    print("Grouping")
    heads = heads.groupby(heads.triple_x)
    tails = tails.groupby(tails.triple_x)

    

    true_pos = 0
    missing = 0
    rr = 0
    # FOR LOOP OVER VALID
    for index, triple in tqdm(test.iterrows()):
        # get valid item
        triple = str(triple[0]) + " " + str(triple[1]) + " " + str(triple[2])
        top10 = ""
        try:
            top10 = heads.get_group(triple)
            top10 = top10.sort_values(by=["confidence"], ascending=False)
            top10 = top10.reset_index()

            try:
                triple_idx = int(top10.loc[top10["prediction"] == triple].index[0])
                rr += 1.0/triple_idx
            except:
                rr += 0
            # # Rank
            if triple in top10["prediction"][0:10].values:
                true_pos += 1
            # if triple_idx 
        except:
            missing += 1

        try:
            top10 = tails.get_group(triple)
            top10 = top10.sort_values(by=["confidence"], ascending=False)
            top10 = top10.reset_index()

            try:
                triple_idx = int(top10.loc[top10["prediction"] == triple].index[0])
                rr += 1.0/triple_idx
            except:
                rr += 0
            # # Rank
            if triple in top10["prediction"][0:10].values:
                true_pos += 1
        except:
            missing += 1

    print(path_predictions)
    print("true positives = " + str(true_pos))
    print("missing = " + str(missing))
    print("hits at 10 = " + str(true_pos/(len(test)*2)))
    print("MRR = " + str(rr/(len(test)*2)))

    results = []
    try:
        results = pd.read_csv(output_path)
    except:
        results = pd.DataFrame(columns=["data_set", "true positives", "missing", "hits@10"])
    results.loc[results.size] = [path_predictions, true_pos, missing, true_pos/len(valid)]
    results.to_csv(output_path, index=False)

if __name__ == "__main__":
    res = evaluate(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5], sys.argv[6], sys.argv[7], sys.argv[8] == 'True')
    print(res)
