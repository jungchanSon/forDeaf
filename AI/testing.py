# -*- coding: utf-8 -*-
"""
Created on Mon Jul 25 16:25:51 2022

@author: User
"""

import tensorflow as tf
from tensorflow import keras
import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder
from keras.utils import to_categorical

def testing(model_path,path_pkl):
    eva,test_pre,test_predicted = model_evaluate(model_path, path_pkl)
    print(eva)
    
    return test_pre,test_predicted
    
def model_evaluate(model_path,path_pkl):    
    #path_pic = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature_df.pkl'
    model = keras.models.load_model(model_path)
    feature_df = pd.read_pickle(path_pkl)

    X = np.array(feature_df.mfccs.tolist())
    y = np.array(feature_df.class_label.tolist())
    y = to_categorical(LabelEncoder().fit_transform(y))

    n_columns = 87
    n_row = 40
    n_channels = 1
    n_classes = 10

    with tf.device('/cpu:0'):
        X = tf.reshape(X,[-1,n_row,n_columns,n_channels])
        
    test_pre = model.predict(X,verbose=0)
    test_predicted = test_pre.argmax(axis=-1)
    
    return model.evaluate(X,y),test_pre,test_predicted
    
'''
0 – aircon
1 – car_horn
2 – children_playing
3 – dog_bark
4 – drilling
5 – engine_idiling
6 – gun_shot
7 – jackhammer
8 – siren
9 – street_music
'''

model_path = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/test_0.1'
path_pic = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature_df.pkl'
test_pre,test_predicted = testing(model_path,path_pic)

print('예측 = ',test_predicted)

res = np.zeros([7857,2])
for idx, per in enumerate(test_predicted):
    res[idx,0] = test_predicted[idx]
    res[idx,1] = test_pre[idx,per]
    
print('정답확률 = ',res)

#7857개의 소리데이터 -> sound generator를 통해 더 많은 데이터를 생성하여 학습필요
#일부 성능을 저하시킨다고 생각되는 sound(aircon,gun_shot...) 제거 필요
#-> 제거한 sound 는 test case 에 포함시켜 일정 threshhold 이하로 나오는지 테스트
#일정 Threshhold 이하는 undefined태그 


