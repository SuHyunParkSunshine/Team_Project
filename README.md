# 다중 객체 이미지 판별 웹 서비스

## 개발 환경
- 개발환경
    - IDE : vscode
    - Data
        - 오픈 소스 머신러닝 라이브러리 : pytorch 2.0.0
        - model : utralytics -> yolo 8
    - 개발언어
        - Python 3.9.13
        - Flask 2.3.2
## 2023/08/19
- 프로젝트 회의 및 계획서작성
## 2023/08/23
- yolo v8을 써서 다중객체를 instance segmentation 시도
- 학습을 돌리기 위해서는 이미지의 사이즈 리사이징 및 방향 조절이 필요
- roboflow라는 사이트에서 이미지에 있는 객체들을 라벨링하고 데이터 셋으로 만듬
- colab을 써서 데이터 셋을 학습시킴
## 2023/08/24
- yolo8 모델로 라벨링한 데이터를 학습시킨 결과 overfitting되는 것을 확인
- 변수를 조금씩 바꾸거나 데이터셋의 양을 늘려야함
- 3d카메라를 고정시켜서 최적의 data를 뽑을 수 있는 위치를 설정해야함
## 2023/08/25
- 학습된 모델 저장하는 법 확인 (다운로드 받고 이름명 바꾸기)
- 학습된 모델로 이미지 객체 판별가능

## 2023/08/26
- 모델에서 segmentation된 이미지와 x,y좌표가 나오는 txt파일 확인
- 학습모델 5개 만듬

## 2023/08/27
- predict을 사용해서 다양한 정보 나오게 할 수 있는 것 확인
- ex) conf = 0.6 정확도 0.6이상만 나오게, save_txt = True txt파일 저장하는 파라미터 등등

## 2023/08/28
- cuda로 로컬에서 gpu 사용하려고 했고 성공 (vscode가 아니라 jupyter notebook으로 해야함)
- 속도가 colab보다 느려서 포기

## 2023/08/29
- flask를 사용해서 back과 연결 확인
- back에서 받은 이미지 파일을 모델에 넣음
- 모델에서 생성된 정보 전처리해서 원하는 형태로 보낼 수 있는 것 확인
- 이제 ply 처리하는 코드 작성해야함
