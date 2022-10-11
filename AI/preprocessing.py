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
    
    ##추가해야할 사항
    ##
    ##audio(원본 신호) 의 값 확인후, 최대 값 = 최대 파형 인지 확인 후,
    ##일치 시, 최대 값 기준 앞뒤 0.5초로 잘라서 전부 다 1초의 길이의 데이터로 가공.
    ##만약 1초 이내의 데이터일시 그대로 두고, 1초로 패딩.

    print('sr = ',sr)

    ##추가해야할 사항
    ## 데이터를 1초로 변환하면, stft 변환의 인자 값들 수정필요
    D = np.abs(librosa.stft(audio, n_fft=4096, win_length=4096, hop_length=1024))
    mfcc = librosa.feature.mfcc(S = librosa.power_to_db(D),sr=sr,n_mfcc=40)

    ##추가해야할 사항
    ## 파일의 길이를 모두 같게 변환하면 패딩 불필요 할듯.
    ## 안드로이드에서 필요시 패딩
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
        ##추가해야할 사항
        ## 각 폴더에서 검증용 데이터 추출.
        ## 폴더당 100개 정도(최대한 균형 맞게)
        ## 추출이후 검증용 pkl파일(데이터 파일) 생성해야함.
        
        ## k-fold cross validation 가능하면 적용하면 좋을듯.

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
