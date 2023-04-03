# Evaluating Knowledge Graph Embeddings for Protein Function
A group project in the context of the [CDT in Biomedical AI](https://web.inf.ed.ac.uk/cdt/biomedical-ai), at the [University of Edinburgh](https://www.ed.ac.uk/).  
Authors: [Aryo Pradipta Gema](https://aryopg.github.io/), [Dominik Grabarzcyk](https://www.linkedin.com/in/dominik-grabarczyk/), [Wolf De Wulf](https://wolfdewulf.eu)   
Supervisors: [Dr. Javier Alfaro](https://www.proteogenomics.ca/), [Dr. Pasquale Minervini](https://neuralnoise.com/), [Dr. Antonio Vergari](http://nolovedeeplearning.com/), [Dr. Ajitha Rajan](https://homepages.inf.ed.ac.uk/arajan/) 

## 1. Installation
Recompile AnyBURL using [scripts/recompile_anyburl.sh](./scripts/recompile_anyburl.sh)

## 2. Data
Data used is taken from [biokge](https://git.ecdf.ed.ac.uk/s2412861/biokge/) to obtain the same splits. The `train.csv`, `valid.csv` and `test.csv` can be used directly.

## 3. Running scripts
Configuration files can be found ing the [config/](./config) folder. An example using default settings for learning and inferrence can be found in [config/base](./config/base)

To train one can update the [scripts/train.sh](./scripts/train.sh) file. Inference should loop over the provided time intervals.

Evaulation can be done using the [scripts/evaluate.sh](./scripts/evaluate.sh) file, where paths should be updated to reflect local paths.

## 4. Questions
Feel free to contact any of the authors via email if you have questions. 
