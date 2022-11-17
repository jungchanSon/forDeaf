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
def make_model(n_columns,n_row,n_classes):
    model = keras.Sequential()

    model.add(layers.InputLayer(input_shape=(n_row,n_columns,1)))
    model.add(layers.Conv2D(filters=32,kernel_size=3,activation='relu'))
    model.add(layers.MaxPooling2D(pool_size=2))
    model.add(layers.Dropout(0.3))
    
    model.add(layers.Conv2D(kernel_size=3,filters=64,activation='relu'))
    model.add(layers.MaxPooling2D(pool_size=2))
    model.add(layers.Dropout(0.3))
    
    model.add(layers.Conv2D(kernel_size=3,filters=128,activation='relu'))
    model.add(layers.MaxPooling2D(pool_size=2))
    model.add(layers.Dropout(0.3))
    
    model.add(layers.GlobalAveragePooling2D())
    model.add(tf.keras.layers.Dense(units=n_classes,activation='softmax'))
    return model

#모델 수정 후 모델 구조 확인
def show_model():
    ###mfccs 구조
    n_columns = 173
    n_row = 40
    n_classes = 10
    
    model = make_model(n_columns,n_row,n_classes)
    model.summary()
    return

#학습 시작
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
    n_columns = 173
    n_row = 40
    n_classes = 3

    with tf.device('/cpu:0'):
        x_train = tf.reshape(x_train,[-1,n_row,n_columns])
        x_test = tf.reshape(x_test,[-1,n_row,n_columns])
    
    print(x_train.shape)
    print(x_test.shape)
    
    model = make_model(n_columns,n_row,n_classes)
    model.summary()

    ###하이퍼 파라미터
    ###epoch, batch_size, lr, opt, loss function
    ###6, 128, 0.001, Adam, categorical_crossentropy
    epoch = 6
    batch_size = 128
    learning_rate = 0.001
    opt = keras.optimizers.Adam(learning_rate=learning_rate)
    
    model.compile(loss='categorical_crossentropy',optimizer=opt,metrics=['accuracy'])
    
    print(' epoch = ',epoch,'\n','batch_size = ',batch_size,'\n', 'learning_rate = ',learning_rate,'\n', '학습 시작')
    history = model.fit(x_train,y_train,batch_size=batch_size,epochs=epoch)
    
    results = model.evaluate(x_test, y_test, batch_size=128)
    print('\n# Evaluate on test data')
    print('test loss, test acc:', results)
    return model,history

#plt plot출력(acc,loss)
def plot_history(history) :
    plt.plot(history.history['accuracy'], label='accuracy')
    plt.plot(history.history['loss'], label='loss')
    plt.legend()
    return

#모델 세이브
def save_model(model,name):
    model.save(name)
    print(name,'으로 모델 저장 완료.')
    return

#ftlite 로 변환
def tflite_convert(model,name):
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    open(name, "wb").write(tflite_model)
    return
    
#시작
def run():
    model,history = train('C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature_df.pkl')
    plot_history(history)
    return

#변수들 보고싶으면 해제 후 실행
model,history = train('C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature_df.pkl')
plot_history(history)
