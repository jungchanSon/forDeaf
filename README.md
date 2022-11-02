# ForDeaf
## 청각 장애인을 위한 보조기기 & 어플리케이션 & AI 개발

--------------------
### 개발 목적
> 청각 장애인은 자동차 경적소리, 공사 소리 등 소리를 인지 하지 못한다  
> 이는 불의의 사고 발생 시 큰 사고로 이어질 수 있고, 이를 방지하고자 청각 장애인을 위한 보조기기와  
> 그에 필요한 어플리케이션, AI를 개발해 청각 장애인의 주변 상황 인식 능력을 향상시키고자 한다  
--------------------
### 동작 방식 설명
> 안드로이드 어플리케이션을 통한 소리 데이터 분석 -> AI 모델을 통한 위험 신호 분류 -> 결과에 따라 보조기기에 진동으로 피드백  
> ![image](https://user-images.githubusercontent.com/113767545/198918223-1a1c0b23-df07-4d69-b4f2-2f9eb3ea9064.png)  

--------------------
### 개발에 사용된 데이터 셋
>![image](https://user-images.githubusercontent.com/113767545/197114140-687f12ed-ec37-4ad1-b64f-c95bde73f482.png)
> class ID:  
> 0 - air_conditioner  
> 1 - car_horn  
> 2 - children_playing  
> 3 - dog_bark  
> 4 - drilling  
> 5 - engine_idling  
> 6 - gun_shot  
> 7 - jackhammer  
> 8 - siren  
> 9 - street_music
--------------------
### 전처리 완료된 데이터 셋 설명
> #### feature_df.pkl  
>> 모든 데이터 파일에 대해 최대 피크지점을 중심으로 앞, 뒤 0.5초 만큼 잘라내어 변환한 MFCC와 class label을 X,y로 가짐  
>> X = MFCCS, y = class_label  
>> (만약 앞, 뒤 길이가 짧아 모자라면 그만큼 앞, 뒤로 0.n초 만큼 추가하여 자름, 1.0초 이내의 
>> 파일일시 자를 수 있는 만큼만 자르고, zero-padding)
>> ### 예시 1
>> ![image](https://user-images.githubusercontent.com/113767545/197115631-3df0a239-7a6a-419f-9304-2e2d0f35e3cb.png)
>> ![image](https://user-images.githubusercontent.com/113767545/197115638-2983a379-8c8b-4b99-8566-0eb621ff105b.png)
>> ### 예시 2
>> ![image](https://user-images.githubusercontent.com/113767545/197115684-bbd0503a-67bc-46e3-90a1-fcac56575a2a.png)
>> ![image](https://user-images.githubusercontent.com/113767545/197115691-52c69808-f2a7-4321-8df4-7415ca4b73d0.png)  
> #### val_feature_df.pkl  
>> 각 폴더당 10%(약 80개)를 추출하여 위와 같은 전처리 과정을 거친 검증용 데이터  
--------------------
### 어플리케이션 설명
> #### 추가  
>> 추가추가  
>> 추가추가.....  
> #### 추가  
>> 추가추가  
>> 추가추가.....  
> #### 위험 신호 선택 기능  
>> 초기 분류한 10가지의 소리 중 알림을 원하는 소리를 선택가능  
> #### 감지 민감도 설정 기능  
>> 0.XXXX 의 정확도 이상이면 알림 -> 0.XXXX 를 슬라이드바를 이용하여 조절가능  
--------------------
### 하드웨어 설명
> ![image](https://user-images.githubusercontent.com/113767545/197116603-6e9e940f-b44f-4c5f-a540-cf8c40547266.png)
> ![image](https://user-images.githubusercontent.com/113767545/197117951-9ddcaca0-14c1-4b54-8392-9830119d6312.png)  
> 100원 짜리 동전보다 작은 크기인 블루투스 모듈을 이용하여 제품을 만듦  
> 초기 세팅이 완료된 블루투스 모듈에 저항, 전선, 수은 건전지, 진동 모터 등을 연결하여 완성  
> 완성된 제품을 손목밴드 등에 부착하여 사용함  
--------------------
### 모델의 정확도  
> 검증 데이터를 이용하여 테스트 한 결과 약 90%의 정확도를 보임  



