# -*- coding: utf-8 -*-
"""
Created on Thu Jul 21 20:59:39 2022

@author: User
"""
import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow import keras
from keras.utils import to_categorical
from tensorflow.keras import layers
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt

#model 구조
def make_model(n_columns,n_row,n_channels,n_classes):
    model = keras.Sequential()

    model.add(layers.Conv2D(input_shape=(n_row,n_columns,n_channels),filters=16,kernel_size=2,activation='relu'))
    model.add(layers.MaxPooling2D(pool_size=2))
    model.add(layers.Dropout(0.2))
    
    model.add(layers.Conv2D(kernel_size=2,filters=32,activation='relu'))
    model.add(layers.MaxPooling2D(pool_size=2))
    model.add(layers.Dropout(0.2))
    
    model.add(layers.Conv2D(kernel_size=2,filters=64,activation='relu'))
    model.add(layers.MaxPooling2D(pool_size=2))
    model.add(layers.Dropout(0.2))
    
    model.add(layers.Conv2D(kernel_size=2,filters=128,activation='relu'))
    model.add(layers.MaxPooling2D(pool_size=2))
    model.add(layers.Dropout(0.2))
    
    model.add(layers.GlobalAveragePooling2D())
    model.add(tf.keras.layers.Dense(units=10,activation='softmax'))
    return model

def show_model():
    ###mfccs 구조
    n_columns = 87
    n_row = 40
    n_channels = 1
    n_classes = 10
    
    model = make_model(n_columns,n_row,n_channels,n_classes)
    model.summary()
    return
    
def train(path_pkl):
    #mfccs와 class label이 저장된 pkl로드
    feature_df = pd.read_pickle(path_pkl)

    X = np.array(feature_df.mfccs.tolist())
    y = np.array(feature_df.class_label.tolist())
    #1~10 class label to categorical
    y = to_categorical(LabelEncoder().fit_transform(y))

    #train_test_split, test_size = 0.2, random_state = 42
    x_train,x_test,y_train,y_test = train_test_split(X,y,test_size=0.2,random_state = 42)

    ###mfccs 구조
    n_columns = 87
    n_row = 40
    n_channels = 1
    n_classes = 10

    with tf.device('/cpu:0'):
        x_train = tf.reshape(x_train,[-1,n_row,n_columns,n_channels])
        x_test = tf.reshape(x_test,[-1,n_row,n_columns,n_channels])
    
    print(x_train.shape)
    print(x_test.shape)
    
    model = make_model(n_columns,n_row,n_channels,n_classes)
    model.summary()

    ###하이퍼 파라미터
    ###epoch, batch_size, lr, opt, loss function
    ###50, 64, 0.001, Adam, categorical_crossentropy
    epoch = 30
    batch_size = 64
    learning_rate = 0.001
    opt = keras.optimizers.Adam(learning_rate=learning_rate)
    
    model.compile(loss='categorical_crossentropy',optimizer=opt,metrics=['accuracy'])
    
    print(' epoch = ',epoch,'\n','batch_size = ',batch_size,'\n', 'learning_rate = ',learning_rate,'\n', '학습 시작')
    history = model.fit(x_train,y_train,batch_size=batch_size,epochs=epoch)
    
    results = model.evaluate(x_test, y_test, batch_size=64)
    print('\n# Evaluate on test data')
    print('test loss, test acc:', results)
    return model,history

def plot_history(history) :
    plt.plot(history.history['accuracy'], label='accuracy')
    plt.plot(history.history['loss'], label='loss')
    plt.legend()
    return

def save_model(model,name):
    model.save(name)
    print(name,'으로 모델 저장 완료.')
    return

def run():
    model,history = train('C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature_df.pkl')
    plot_history(history)
    return

model,history = train('C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature_df.pkl')
plot_history(history)
