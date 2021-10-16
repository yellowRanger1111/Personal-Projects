The basic requirements for assignment 2 mirror those of assignment 1. The main difference
is the addition of PyTorch (https://pytorch.org/).

To recap, these were the instructions required to install assignment 1 on Windows.

*If you re-use assignment 1's virtual environment, skip these steps*

	py -3 -m venv flatenv
	flatenv\Scripts\activate
	pip install --upgrade pip
	pip install wheel
	pip install -r requirements.txt

Command-line instruction to install PyTorch using pip follow.

Windows / Linux:

	pip install torch==1.6.0+cpu -f https://download.pytorch.org/whl/torch_stable.html

Mac OSX:

	pip install torch


To check that your installation of PyTorch is working, try running the single-agent
training script, as follows:

	cd src\
	python single_agent_training.py
