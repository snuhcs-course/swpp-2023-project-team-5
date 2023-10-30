import numpy as np

def rmse(ans, pred):
    return np.sqrt(np.mean(np.square(ans-pred)))

def smape(ans, pred):
    return (100/ans.shape[0] * np.sum(np.abs(ans-pred) / (np.abs(ans) + np.abs(pred))))
