from statsmodels.graphics.tsaplots import plot_acf, plot_pacf
from statsmodels.tsa.statespace.varmax import VARMAX
from statsmodels.tsa.api import VAR
from statsmodels.tsa.stattools import grangercausalitytests, adfuller

import argparse
import matplotlib.pyplot as plt
import statsmodels.api as sm
import pandas as pd
import numpy as np
import matplotlib
matplotlib.use("agg")
from errors import rmse, smape



def print_header(title):
    print(f"\n====================={title}=====================")



def get_data(base_path):
    """Get Data"""
    macro_data = pd.read_excel(f'{base_path}/data/FCR_Data_combined.xlsx', parse_dates=['Year'], index_col='Year')
    macro_data = macro_data[2:]
    cols_to_drop = ['Unnamed: 1', 'Weather', 'Max', 'Min', 'Unnamed: 7', 'Direction', 'Humidity.1', 'Cloud.1', 'days from', 'Unnamed: 17', 'Unnamed: 19']
    macro_data = macro_data.drop(columns=cols_to_drop)
    print(f"shape of original data: {macro_data.shape}")

    return macro_data



def adf_test(macro_data):
    stationary_bound = 0.05
    """Perform ADF test on each variable, and return nonstationary variables"""
    nonstationary_cols = []

    for i in range(9):
        col_name = macro_data.columns[i]
        data = macro_data[col_name]
        adfuller_res = adfuller(data)
        is_stationary = False

        if adfuller_res[1] < stationary_bound:
            is_stationary = True
        else:
            nonstationary_cols.append(col_name)

        print(f"[{col_name}] is stationary ({is_stationary})\nADF Statistic: {adfuller_res[0]}\tp-value: {adfuller_res[1]}\n")
    
    return nonstationary_cols



def diff(macro_data, nonstationary_cols):
    stationary_bound = 0.05
    """Updates nonstationary variables of macro_data by getting the diff"""
    for col_name in nonstationary_cols:
        data_diff = macro_data[col_name].diff()[1:]
        macro_data[col_name] = data_diff
        adfuller_res = adfuller(data_diff)    # first order diff
        is_stationary = False
        if adfuller_res[1] < stationary_bound:
            is_stationary = True
        print(f"[{col_name}] is stationary ({is_stationary})\nADF Statistic: {adfuller_res[0]}\tp-value: {adfuller_res[1]}\n")
    
    

def causality_test(lag, macro_data):
    """Conduct Granger Causality Test and returns related lags"""
    stationary_bound = 0.05
    column_pairs = []
    col_names = ['T_mean', 'Wind speed', 'Rain', 'Humidity', 'Cloud', 'Pressure', 'solar radiation*', 'Soil Moisture*', 'FCR status']

    for col_name1 in col_names:
        for col_name2 in col_names:
            if col_name1 == col_name2:
                continue
            column_pairs.append([col_name1, col_name2])

    stationary = {}
    for pair in column_pairs:
        tag = f"{pair[1]}==>{pair[0]}"
        print(f"[{tag}]", end=' ')
        granger = grangercausalitytests(macro_data[pair], lag, verbose=False)
        lagcnt = 0
        for l in granger:
            lag_result = granger[l][0]
            cnt = 0
            for test in lag_result:
                if lag_result[test][1] < stationary_bound:
                    cnt += 1
                    
            if cnt == 4:
                print(f"{l}", end=' ')
                if tag in stationary:
                    stationary[tag].append(l)
                else:
                    stationary[tag] = [l]
                lagcnt += 1
        if lagcnt == 0:
            print("NO RELATION")
        else:
            print()

    return stationary



def reduce_var(macro_data):
    # TODO: reduce variables
    print("Returning default variables for now...")
    vars  = ['T_mean', 'Rain', 'Humidity', 'Cloud', 'FCR status']
    return macro_data[vars]



def main(args):
    macro_data = get_data(args.base_path)
    n_test = int(macro_data.shape[0] * args.test_set_ratio)
    train_fcr_val = macro_data['FCR status'].values[-n_test-1]

    print_header("Checking Stationarity (ADF Test)")
    nonstationary_cols = adf_test(macro_data)

    print_header("Get Diff")
    if args.model == 'var':
        # If VAR model, do diff, else (if ARIMA), do not diff
        diff(macro_data, nonstationary_cols)
        

    # getting rid of first data because of diff (first row becomes NaN)
    macro_data = macro_data[1:]

    print_header("Granger Causality Test")
    causality_relation = causality_test(args.lag, macro_data)

    print_header("Reducing Variables")
    macro_data = reduce_var(macro_data)

    # Train Test split
    
    train_data = macro_data[:-n_test].astype('float64') 
    test_data = macro_data[-n_test:].astype('float64') 

    print_header("Initializing and Training model / Getting Predictions")
    if args.model == 'var':
        model = VAR(train_data)
        sorted_order = model.select_order(10)
        print(sorted_order.summary())

        """Checked sorted order. 2 lags was best"""
        fitted_model = model.fit(2)
        predictions = fitted_model.forecast(train_data.values[-fitted_model.k_ar:], steps=n_test)

        for i, p in enumerate(predictions[:, -1]):
            if i == 0:
                predictions[i][-1] = p + train_fcr_val
                test_data['FCR status'].values[i] = test_data['FCR status'].values[i] + train_fcr_val
            else:
                predictions[i][-1] = predictions[i-1][-1] + predictions[i][-1]
                test_data['FCR status'].values[i] = test_data['FCR status'].values[i-1] + test_data['FCR status'].values[i]

    else:
        model = VARMAX(train_data, order=(args.lag, args.ma))
        fitted_model = model.fit()  # 왜 이건 lag 가 필요없지
        predictions = fitted_model.get_prediction(start=len(train_data), end=len(train_data)+n_test-1).predicted_mean.values

    ans = test_data['FCR status'].values
    pred = predictions[:, -1]

    print(f"\n[RMSE error]: {rmse(ans, pred)}")
    print(f"[SMAPE error]: {smape(ans, pred)}")

    test_vs_pred = pd.DataFrame({'Actual': ans, 'Predictions': pred})
    test_vs_pred.plot().get_figure().savefig('prediction.png')


def format_input_data(input_data):
    """
    Formats the input data to match the structure expected by the model.

    Args:
    input_data (dict): A dictionary containing the input data.

    Returns:
    DataFrame: A DataFrame formatted as per the model's input requirements.
    """
    # Assuming input_data is a dictionary where keys are column names
    # and values are the respective data points.
    formatted_data = pd.DataFrame([input_data])

    # If your model expects data in a specific order or with specific transformations,
    # apply those here.
    # For example, if your model expects the columns in a specific order:
    expected_columns = ['T_mean', 'Rain', 'Humidity', 'Cloud']  # Update this as per your model's training data
    formatted_data = formatted_data[expected_columns]

    # Any additional preprocessing steps that were applied to the training data
    # should also be applied here.

    return formatted_data

def predict_fcr_status(input_data, base_path, model_type='var', lag=4, test_set_ratio=0.6):
    # Load and preprocess the dataset
    macro_data = get_data(base_path)
    n_test = int(macro_data.shape[0] * test_set_ratio)
    train_fcr_val = macro_data['FCR status'].values[-n_test - 1]

    # ADF Test
    nonstationary_cols = adf_test(macro_data)

    # Get Diff
    if model_type == 'var':
        diff(macro_data, nonstationary_cols)

    # Remove first data point after differencing
    macro_data = macro_data[1:]

    # Granger Causality Test
    causality_relation = causality_test(lag, macro_data)

    # Reducing Variables
    macro_data = reduce_var(macro_data)

    # Train Test split
    train_data = macro_data[:-n_test].astype('float64')

    # Format the input_data as per the model's requirement
    formatted_input_data = format_input_data(input_data)

    # Initializing and Training model
    # if model_type == 'var':
    #     model = VAR(train_data)
    #     fitted_model = model.fit()  # Example: fitting with 2 lags
    #     prediction = fitted_model.forecast(formatted_input_data, steps=1)[-1]
    # else:
    model = VARMAX(train_data, order=(lag, 0))
    fitted_model = model.fit()
    prediction = fitted_model.get_prediction(start=len(train_data), end=len(train_data) + len(
        formatted_input_data) - 1).predicted_mean.values[-1]

    # Adjusting prediction for FCR status
    adjusted_prediction = prediction + train_fcr_val

    # Assuming 'FCR status' is the last variable in your model,
    # and you want only the first time step prediction.
    fcr_status_prediction = adjusted_prediction[-1]  # Adjust the index as per your model's structure

    return fcr_status_prediction

def main():
    # Example usage
    # Ensure that the keys in this dictionary match your model's expected input features
    input_parameters = {
        'T_mean': 29,   # Example values
        'Rain': 6,
        'Humidity': 0.77,
        'Cloud': 0.49
    }
    base_path = '../'
    fcr_status = predict_fcr_status(input_parameters, base_path)
    print(f"Predicted FCR Status: {fcr_status}")

main()

# if __name__ == '__main__':
#     parser = argparse.ArgumentParser()
#
#     parser.add_argument('--base-path', type=str, default='..')
#     parser.add_argument('--lag', type=int, default=4)
#     parser.add_argument('--ma', type=int, default=0)
#     parser.add_argument('--test-set-ratio', type=float, default=0.1)
#     parser.add_argument('--model', type=str, default='arima')
#
#     args = parser.parse_args()
#     subdir = f"./log/{args.model}-test_set_ratio_{args.test_set_ratio}-MA_{args.ma}"
#
#     assert args.model == 'var' or args.model == 'arima', \
#         "model type should be either var or arima"
#
#     units = {'T_mean': 'Celsius', 'Wind speed': 'km/h', 'Rain': 'mm', 'Humidity': 'Percentage (%)', 'Cloud': 'Percentage (%)', 'Pressure': 'mb', 'solar radiation*': 'W/m^2', 'Soil Moisture*': 'Percentage (%)', 'FCR status': ' '}
#     col_names = ['T_mean', 'Wind speed', 'Rain', 'Humidity', 'Cloud', 'Pressure', 'solar radiation*', 'Soil Moisture*', 'FCR status']
#     stationary_bound = 0.05
#
#     main(args)




