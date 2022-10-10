# -*- coding: utf-8 -*-
"""
Created on Sat Jul  2 22:24:59 2022

@author: User
"""

import librosa
import librosa.display
import pandas as pd
import numpy as np
import os

#file_name에 해당하는 파일의 mfccs를 생성,리턴
def make_mfccs(file_name):
    max_pad_len = 87
    audio,sr = librosa.load(file_name)
   
    print('sr = ',sr)
    D = np.abs(librosa.stft(audio, n_fft=4096, win_length=4096, hop_length=1024))
    mfcc = librosa.feature.mfcc(S = librosa.power_to_db(D),sr=sr,n_mfcc=40)
    pad_width = max_pad_len - mfcc.shape[1]
    mfcc = np.pad(mfcc,pad_width = ((0,0),(0,pad_width)),mode = 'constant')
        
    return mfcc

#mfccs와 class label을 가진 pkl파일 생성, 저장
def make_dataframe(file_path_audio,file_path_csv):
    fold_num = 1
    fold = 'fold' + str(fold_num)
    path = file_path_audio + fold
    path_csv = file_path_csv + '/UrbanSound8k.csv'
    file_list = os.listdir(path)
    file_list_wav = [file for file in file_list if file.endswith(".wav")]
    df = pd.read_csv(path_csv)
    features = []
    
    #폴더 1~10까지 반복
    for i in range(1,10):
        for file_name in file_list_wav:
            print(file_name)
            audio_file = path+'/'+file_name
            
            for idx,name in enumerate(df['slice_file_name']):
                if name == file_name:
                    class_label = df.iloc[idx,6]
                    
            data = make_mfccs(audio_file)
            features.append([data,class_label])
            
        fold_num+=1
                    
    feature_df = pd.DataFrame(features,columns = ['mfccs','class_label'])
    feature_df.to_pickle("feature_df.pkl")
    
    print("feature_df.pkl 생성 완료")
    return

#전처리 실행해주는 함수 -> 컴파일 후 콘솔에서 실행
def pre_processing():
    audio_path = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/dataset/UrbanSound8k/audio/'
    csv_path = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/dataset/UrbanSound8k/metadata'
    make_dataframe(audio_path,csv_path)
    return
