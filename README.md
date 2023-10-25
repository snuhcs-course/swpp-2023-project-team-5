# I'm Pine Thank You

Welcome to the "I'm Pine Thank You" project repository!  

This repository contains documentation and resources related to our project. Please refer to the following Wiki pages for detailed information:

## Table of Contents

- [Design Document](../../wiki/Design-Document)
- [Requirements and Specifications](../../wiki/Requirements-and-Specifications)

## Server Setup
The server is built using the Django framework with Python and is located inside the "backend" folder.
To run the local Django server for testing, you will need the following:

1. Python (version 3.7 or higher)
2. An `.env` file (request from @hyungseok-choi if needed)

### How to Set Up

1. Open a terminal in the root directory and run the following command to create a Python virtual environment named "venv" and install the required packages:
   ```bash
   source ./setup_venv.sh
   ```
2. Change the directory to the "backend" folder:
   ```bash
   cd backend
   ```
3. If you are running the server for the first time or there have been changes to the models, run the following commands to apply migrations:
   ```bash
   python manage.py makemigrations
   python manage.py migrate
   ```
4. Start the Django local server with the following command:
   ```bash
   python manage.py runserver
   ```
