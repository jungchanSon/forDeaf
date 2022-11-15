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
import matplotlib.pyplot as plt

#file_name에 해당하는 파일의 mfccs를 생성,리턴
#state = 'trim' -> use trim_audio
#state = 'stretching' -> use stretching_audio
#state = 'addnoise' -> use shifting_audio
def make_mfccs(file_name,state:str,trim_time=1,term_ratio=0.5,noise_power=10):
    max_pad_len = 173
    time = trim_time
    term = term_ratio
    noise = noise_power
    audio,sr = librosa.load(file_name) #sr = 22050/s
    
    #------------------------------------------------------
    #   wave 확인용 코드
    #   librosa.display.waveshow(audio,sr=sr)
    #   plt.show()
    #------------------------------------------------------
    
    #원본 신호 time초로 trim(최대 peak기준)
    #audio(파형), sr(sampling rate), Time(초) 을 인자로 받음
    if state == 'trim':
        audio = trim_audio(audio,sr,time)
    #original audio 'Time' 초로 stretching
    elif state == 'stretching':
        # print("Before Stretching")
        # librosa.display.waveshow(audio,sr=sr)
        # plt.show()
        audio = stretching_audio(audio,sr,time)
        # print("After Stretching")
        # librosa.display.waveshow(audio,sr=sr)
        # plt.show()
    #trim audio shifting
    elif state == 'shifting':
        audio = trim_audio(audio,sr,time)
        # print("Before Shifting")
        # librosa.display.waveshow(audio,sr=sr)
        # plt.show()
        audio = shifting_audio(audio,term)
        # print("After Shifting")
        # librosa.display.waveshow(audio,sr=sr)
        # plt.show()
    #add whitenoise in trim audio
    elif state == 'addnoise':
        audio = trim_audio(audio,sr,time)
        # print("Before Addnoise")
        # librosa.display.waveshow(audio,sr=sr)
        # plt.show()
        audio = addnoise_audio(audio,noise)
        # print("After Addnoise")
        # librosa.display.waveshow(audio,sr=sr)
        # plt.show()
    else:
        print("state type ERROR")
        return
    
    '''
    D = np.abs(librosa.stft(audio, n_fft=4096, win_length=4096, hop_length=1024))
    mfcc = librosa.feature.mfcc(S = librosa.power_to_db(D),sr=sr,n_mfcc=40)
    '''
    
    #안드로이드 jlobrosa와 맞추기위해 최대한 간단히 mfcc변환
    #n_fft = 512, win_length = n_fft, hop_length = 128, n_mfcc = 40
    mfcc = librosa.feature.mfcc(y=audio,sr=sr,n_fft=512,hop_length=128,n_mfcc=40)
    
    #(40,173) 출력됨
    print(mfcc.shape)
    
    #1초 미만인 mfcc에 대하여 zero padding
    pad_width = max_pad_len - mfcc.shape[1]
    mfcc = np.pad(mfcc,pad_width = ((0,0),(0,pad_width)),mode = 'constant')    
        
    return mfcc

#audio를 1초(가능하면)로 trim해주는 함수
def trim_audio(audio,sr,time):
    '''
    #원본 신호의 정보를 출력해 주는 test부분
    print("Max = ",np.max(audio))
    print("Max_index = ",np.argmax(audio))
    print("Time = ",len(audio)/sr)
    librosa.display.waveshow(audio,sr=sr)
    plt.show()
    '''
    time_back = np.argmax(audio)-(time/2)*sr
    time_front = np.argmax(audio)+(time/2)*sr
    if time_back < 0:
        err = (time/2)*sr - np.argmax(audio)
        time_front+=err
        time_back+=err
    if time_front > len(audio):
        err = time_front - len(audio)
        time_front-=err
        time_back-=err
        
    trim_audio = audio[round(time_back):round(time_front)]
    if len(trim_audio)/sr != time:
        print("Time = ",len(trim_audio)/sr)
    '''
    #바뀐 신호의 정보를 출력해 주는 test 부분
    print("Shape = ",trim_audio.shape)
    librosa.display.waveshow(trim_audio,sr=sr)
    plt.show()
    '''
    return trim_audio

#original audio stretching to time
def stretching_audio(audio,sr,time):
    if len(audio)*sr > sr*4:
        audio = trim_audio(audio,sr,4)
    str_time = len(audio) / sr*time
    str_audio = librosa.effects.time_stretch(audio,rate=str_time)
    
    return str_audio
    
#term 만큼 잘라서 shifting
def shifting_audio(audio,term):
    audio1 = audio[0:round(len(audio)*term)]
    audio2 = audio[round(len(audio)*term):-1]
    shifting_audio = np.concatenate((audio2,audio1),axis=0)
    
    return shifting_audio
    
#trim audio 에 white_noise 추가
def addnoise_audio(audio,noise_num):
    for i,data in enumerate(audio):
        r_number = np.random.normal() * noise_num
        audio[i]+=r_number
        
    return audio

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
    val_features = []
    car_horn=0
    siren=0
    other=0
    augmentation_data1 = 0
    augmentation_data2 = 0
    augmentation_data3 = 0
   
    #폴더 1~10까지 반복
    for i in range(1,10):
        print("----------",i,"/10","----------")
        for i,file_name in enumerate(file_list_wav):
            print(file_name)
            audio_file = path+'/'+file_name
            
            for idx,name in enumerate(df['slice_file_name']):
                if name == file_name:
                    # car_horn, siren 소리를 제외한 나머지소리를 other로 통일
                    if df.iloc[idx,6] == 1:
                        #car_horn
                        label = 0
                        car_horn+=1;
                    elif df.iloc[idx,6] == 8:
                        #siren
                        label = 1
                        siren+=1
                    else:
                        #other
                        label = 2
                        other+=1
                        
                    class_label = label
            
            #data augmentation0(trim)
            data = make_mfccs(audio_file,'trim')
            if label!=2:
                #data augmentation1(stretching)
                stretching_data = make_mfccs(audio_file,'stretching')
                augmentation_data1+=1
                #data augmentation2(shifting)
                #0.2:0.8, 0.5:0.5, 0.8:0.2 ratio shifting
                for i in range(2,9,3):
                    shifting_data = make_mfccs(audio_file,'shifting',term_ratio=i*0.1)
                    augmentation_data2+=1
                    val_features.append([shifting_data,label])
                #data augmentation3(addnoise)
                addnoise_data = make_mfccs(audio_file,'addnoise')
                augmentation_data3+=1
                
            #각 폴더당 약 80개 추출하여 검증파일 생성
            print("class_label = ",class_label)
            if i % 10 == 0:
                val_features.append([data,class_label])
                if label!=2:
                    val_features.append([stretching_data,label])
                    val_features.append([addnoise_data,label])
                print("val_features = ",len(val_features))
                print("")
            else:
                features.append([data,class_label])
                if label!=2:
                    features.append([stretching_data,label])
                    features.append([shifting_data,label])
                    features.append([addnoise_data,label])
                print("features = ",len(features))
                print("")
            
        fold_num+=1
                    
    val_feature_df = pd.DataFrame(val_features,columns = ['mfccs','class_label'])
    val_feature_df.to_pickle("val_feature_df.pkl")
    
    feature_df = pd.DataFrame(features,columns = ['mfccs','class_label'])
    feature_df.to_pickle("feature_df.pkl")
    
    print("val_feature_df.pkl 생성 완료")
    print("feature_df.pkl 생성 완료")
    print("----------원본 데이터의 갯수(trim)----------")
    print("car_horn(label 0) = ", car_horn)
    print("siren(label 1) = ", siren)
    print("other(label 2) = ", other)
    print("----------Augmentation data 갯수----------")
    print("stretching_data(aug1) = ",augmentation_data1)
    print("shifting_data(aug2) = ",augmentation_data2)
    print("addnoise_data(aug3) = ",augmentation_data3)
    print("----------Train_data, Validation_data 갯수----------")
    print("val_feature = ",len(val_features))
    print("feature = ",len(features))
    
    return

#사용자 지정 데이터셋 추가함수, Augmentation 4가지 모두 적용(데이터 X 4)
# class_type : car_horn, siren, other 입력
# label은 class_type에 따라 자동 지정됨
# file_type : concatenate, standalone 입력
# concatenate - 기존의 feature_df.pkl 파일에 새로 만든 데이터를 이어붙임
# standalone - 새로 만든 데이터만 따로 저장
def generate_usermade_dataset(class_type:str,file_type:str):
    path = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/dataset/'
    audio_path = path + class_type
    file_list = os.listdir(audio_path)
    file_list_wav = [file for file in file_list if file.endswith(".wav")]
    features = []
    
    if class_type == "car_horn": class_label = 0
    elif class_type == "siren":  class_label = 1
    elif class_type == "other":  class_label = 2
    
    for i, file_name in enumerate(file_list_wav):
        print(i," : ",file_name)
        audio_file = audio_path + '/' + file_name
        
        #data augmentation0(trim)
        trim_data = make_mfccs(audio_file,'trim')
        #data augmentation1(stretching)
        stretching_data = make_mfccs(audio_file,'stretching')
        #data augmentation2(shifting)
        #0.2:0.8, 0.5:0.5, 0.8:0.2 ratio shifting
        for i in range(2,9,3):
            print(i*0.1,":",1-(i*0.1)," ratio shifting")
            shifting_data = make_mfccs(audio_file,'shifting',term_ratio=i*0.1)
            features.append([shifting_data,class_label])
        #data augmentation3(addnoise)
        addnoise_data = make_mfccs(audio_file,'addnoise')
        
        features.append([trim_data,class_label])
        features.append([stretching_data,class_label])
        features.append([addnoise_data,class_label])
        
    feature_df = pd.DataFrame(features,columns = ['mfccs','class_label'])
        
    if file_type == "concatenate":
        before_df = pd.read_pickle('C:/Users/User/Desktop/학교/전남대/캡스톤디자인/feature_df.pkl')
        concatenate_df = pd.concat([before_df,feature_df], ignore_index=True)
        concatenate_df.to_pickle("feature_df.pkl")
        print("----------concatenate complete----------")
        print("feature_df.pkl 생성 완료")
    elif file_type == "standalone":
        feature_df.to_pickle("usermade_dataset.pkl")
        print("----------standalone complete----------")
        print("usermade_dataset.pkl 생성 완료")
        print("features = ",len(features))
    
    return


#전처리 실행해주는 함수 -> 컴파일 후 콘솔에서 실행
def pre_processing():
    audio_path = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/dataset/UrbanSound8k/audio/'
    csv_path = 'C:/Users/User/Desktop/학교/전남대/캡스톤디자인/dataset/UrbanSound8k/metadata'
    make_dataframe(audio_path,csv_path)
    return
