import numpy as np
import pandas as pd

data = pd.read_csv("https://archive.ics.uci.edu/ml/machine-learning-databases/iris/iris.data", header=None, )

encode = {'Iris-setosa': 0, 'Iris-versicolor': 1, 'Iris-virginica': 2}

data[5] = [None] * data.shape[0]

for i in data.index:
    data.at[i, 5] = encode[data.at[i, 4]]

data = data.sample(frac=1)  # shuffling

train_test_split_percentage = 0.7

number_of_rows = data.shape[0]
number_of_train = int(number_of_rows * train_test_split_percentage)

# Splitting the data into test and train
train = data[0:number_of_train]
test = data[number_of_train:]

print(train.shape, test.shape)

X_train = train[[0, 1, 2, 3]]
X_test = test[[0, 1, 2, 3]]
y_train = train[5]
y_test = test[5]

print(len(X_train), len(X_test))

def cost(x, y, b):
    m = len(y)
    j = np.sum((x.dot(b) - y) ** 2) / (2 * m)
    return j

def train(x, y, b, alpha, n_iter):
    cost_his = [0] * n_iter
    m = len(y)

    for i in range(n_iter):
        h = x.dot(b)
        loss = h - y
        gradient = x.T.dot(loss) / m
        b = b - alpha * gradient
        cos = cost(x, y, b)
        cost_his[i] = cos
    return b, cost_his

b = np.zeros(X_train.shape[1])
alpha = 0.005
n_iter = 2000

newB, cost_his = train(X_train, y_train, b, alpha, n_iter)

y_pred = []
correct = 0
for j in X_test.index:
    y = 0
    for i in range(len(newB)):
        y += (X_test.at[j, i] * newB[i])
    y_pred.append(round(y))
    if (round(y) == y_test[j]):
        correct += 1

print((correct / len(y_test)) * 100)

part = int(data.shape[0] / 4)

parts_of_data = []
start = 0
end = part
for i in range(3):
    parts_of_data.append(data[start:end])
    start += part
    end += part
parts_of_data.append(data[start:])

train_dfs = []
test_dfs = []
for i in range(4):
    test_dfs.append(parts_of_data[i])
    temp = parts_of_data.pop(i)
    train_dfs.append(pd.concat(parts_of_data))
    parts_of_data.insert(i, temp)

for ii in range(4):
    X_train = train_dfs[ii][[0, 1, 2, 3]]
    X_test = test_dfs[ii][[0, 1, 2, 3]]
    y_train = train_dfs[ii][5]
    y_test = test_dfs[ii][5]
    b = np.zeros(X_train.shape[1])
    alpha = 0.005
    n_iter = 2000
    newB, cost_his = train(X_train, y_train, b, alpha, n_iter)
    y_pred = []
    correct = 0
    for j in X_test.index:
        y = 0
        for i in range(len(newB)):
            y += (X_test.at[j, i] * newB[i])
        y_pred.append(round(y))
        if (round(y) == y_test[j]):
            correct += 1
    print("Accuracy of flod", ii + 1, (correct / len(y_test)) * 100)

