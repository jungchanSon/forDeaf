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
from sklearn.metrics import confusion_matrix

def testing(model_path,path_pkl,model_type:str):
    if model_type == "tensorflow":
        eva,test_pre,test_predicted,cm = model_evaluate(model_path, path_pkl)
    elif model_type == "tflite":
        test_pre,test_predicted = model_evaluate_tflite(model_path, path_pkl)
    
    return test_pre,test_predicted,cm
    
def model_evaluate(model_path,path_pkl):    
    #path_pic = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature_df.pkl'
    model = keras.models.load_model(model_path)
    feature_df = pd.read_pickle(path_pkl)
    
    #print(feature_df['class_label'].value_counts())
    
    print(feature_df.shape)
    
    X = np.array(feature_df.mfccs.tolist())
    y = np.array(feature_df.class_label.tolist())
    real_y = y
    y = to_categorical(LabelEncoder().fit_transform(y))
    
    print(X.shape)
    test_pre = model.predict(X,verbose=0)
    test_predicted = test_pre.argmax(axis=-1)
    
    cm = confusion_matrix(real_y,test_predicted)
    return model.evaluate(X,y),test_pre,test_predicted,cm
    
def model_evaluate_tflite(model_path, path_pkl):
    interpreter = tf.lite.Interpreter(model_path)
    interpreter.allocate_tensors()
    
    feature_df = pd.read_pickle(path_pkl)
    
    X = np.array(feature_df.mfccs.tolist())
    
    input_details = interpreter.get_input_details()
    interpreter.set_tensor(input_details[0]['index'],X)
    interpreter.invoke()
    
    output_details = interpreter.get_output_details()
    
    test_pre = interpreter.get_tensor(output_details[0]['index'])
    test_predicted = test_pre[0][test_pre.argmax()]
    
    return test_pre,test_predicted
    
    
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
||||| 위에서 아래로 변환함
0 - car_horn
1 - siren
2 - other
'''

#모델경로와 데이터파일(pkl) 파일 경로 수정 필요
model_path = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/test2'
tflite_model_path = 'C:/git/forDeaf/AI/Model/For_Deaf_1.0_withmetadata.tflite'
path_pic = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/val_feature_df.pkl'
test = pd.read_pickle(path_pic)

#tensorflow 모델 테스트
test_pre,test_predicted,cm = testing(model_path,path_pic,"tensorflow")

#tensorflow lite 모델 테스트
#test_pre,test_predicted = testing(tflite_model_path,path_pic,"tflite")

res = np.zeros([3987,2])
for idx, per in enumerate(test_predicted):
    res[idx,0] = test_predicted[idx]
    res[idx,1] = test_pre[idx,per]
    
print('정답확률 = ',res)

# temp = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature/feature_df(1.0ver epoch10 shifting 1번).pkl'
# temp_df = pd.read_pickle(temp)



