# 🚀 SoundMate(공사 중)

> 사용자의 감정을 추론하여 음악을 추천해주는 챗봇앱입니다.

<br>

## 📖 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [주요 기능](#-주요-기능)
3. [미리보기](#%EF%B8%8F-미리보기)
4. [사용 기술](#%EF%B8%8F-사용-기술)
5. [설치 및 실행 방법](#%EF%B8%8F-설치-및-실행-방법)
6. [팀원 소개](#%E2%80%8D%E2%80%8D%E2%80%8D-팀원-소개)
   
<br>

## 📌 프로젝트 소개

이 프로젝트는 사용자의 현재 기분에 맞는 음악을 추천해주는 간단한 챗봇 앱입니다.

사용자가 입력한 문장을 GPT API로 전송하여 감정을 분석하고, 분석된 감정 키워드를 YouTube API에 전달하여 관련 플레이리스트를 찾아 추천하는 방식으로 작동합니다.

여러 외부 API를 조합하여 하나의 서비스를 만드는 통합(Integration) 경험을 목표로 제작되었습니다.

<br>

## ✨ 주요 기능

- **텍스트 감정 분석 기반 음악 추천:** 사용자가 입력한 문장의 긍정, 부정, 중립 등 감정 뉘앙스를 자연어 처리 모델(GPT API)을 통해 분석하고, 해당 감정에 어울리는 음악 플레이리스트를 추천합니다.  
- **이미지 감정 분석 기반 음악 추천:** 사용자가 업로드한 사진 속 인물의 표정을 분석하여 감정을 추론하고, 그에 맞는 분위기의 음악을 추천해주는 기능입니다.
- **자연스러운 챗봇 상호작용:** 단순한 명령어가 아닌, 일상적인 대화를 통해 사용자와 자연스럽게 상호작용하며 음악 추천을 제공합니다.

<br>

## 🖼️ 미리보기
<img width="425" height="931" alt="image" src="https://github.com/user-attachments/assets/5b2cbf48-3b80-4622-8f79-59db09d07c1c" />

<br>

## 🛠️ 사용 기술

- **Backend:** `Java`, `Spring Boot`, `Spring Data JPA`
- **Mobile (Frontend):** `Kotiln (Android)`
- **Database:** `MySQL`
- **APIs & Others:** `OpenAI GPT API`, `YouTube API`
<br>

## ⚙️ 설치 및 실행 방법

```bash
이 프로젝트는 Android환경에서 동작합니다. 실행을 위해 Android Studio가 필요합니다.
또한 Spring Boot(Backend)로 구동되므로 정상적인 앱 구동을 위해 반드시 백엔드 서버를 먼저 실행해 주세요.

# 1. 저장소 클론
git clone https://github.com/Yun-Gi/SoundMate.git

# 2. 백엔드 서버 실행
프로젝트 내 SoundMateB 폴더를 IDE로 열고,
src/main/java/.../Application.java 파일을 찾아 실행(Run)합니다.

# 3. 애플리케이션 실행
Android Studio를 실행하고 프로젝트의 android 폴더를 열고 Gradle 동기화(Sync)가 완료될 때까지 기다립니다.
그 후 상단 툴바에서 에뮬레이터 또는 연결된 기기를 선택하고 Run버튼을 누릅니다.
```

<br>

## 👨‍👩‍👧‍👦 팀원 소개

| 이름 | 역할 |  
| 이윤기 | Android 앱 구현 / DB 설계 및 API 개발 |  
| 최광민 | 디자인 시안 및 전체 UI 흐름 기획 |  
| 홍정원 | 음악 추천 로직 구현 / 챗봇 메시지 처리 |  

