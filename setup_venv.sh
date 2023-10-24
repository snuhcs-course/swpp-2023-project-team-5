#!/bin/bash

# Check if the current directory has .venv directory
if [ -d .venv ]; then
  echo "Using existing virtual environment 'venv'"
else
  # If .venv doesn't exist, create and activate a virtual environment
  python -m venv .venv
  echo "Created and activated virtual environment 'venv'"
fi

source .venv/bin/activate
echo "Virtual environment activated"

# Install required Python packages using pip
pip install -r backend/requirements.txt
echo "Installed required Python packages"

echo "venv setup complete"
echo "To deactivate the virtual environment, run 'deactivate'"