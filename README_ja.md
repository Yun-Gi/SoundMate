[🇰🇷 한국어(Korean)](README.md) | [🇯🇵 日本語(Japanese)](README_ja.md)
# 🚀 SoundMate

> ユーザーの感情を推論して音楽を推薦するチャットボットアプリです。

<br>

## 📖 目次

1. [プロジェクト紹介](#-プロジェクト紹介)
2. [主な機能](#-主な機能)
3. [プレビュー](#%EF%B8%8F-プレビュー)
4. [使用技術](#%EF%B8%8F-使用技術)
5. [インストール及び実行方法](#%EF%B8%8F-インストール及び実行方法)
6. [チームメンバー紹介](#%E2%80%8D%E2%80%8D%E2%80%8D-チームメンバー紹介)
   
<br>

## 📌 プロジェクト紹介

本プロジェクトは、ユーザーの現在の気分に合った音楽を推薦するシンプルなチャットボットアプリです。

ユーザーが入力した文章をGPT APIに送信して感情を分析し、分析された感情キーワードをYouTube APIに渡して関連するプレイリストを検索・推薦する仕組みで動作します。

複数の外部APIを組み合わせて一つのサービスを作り上げる統合(Integration)の経験を目標に制作されました。

<br>

## ✨ 主な機能

- **テキスト感情分析に基づく音楽推薦:** ユーザーが入力した文章の肯定、否定、中立などの感情のニュアンスを自然言語処理モデル(GPT API)を通じて分析し、その感情に合った音楽プレイリストを推薦します。
- **画像感情分析に基づく音楽推薦:** ユーザーがアップロードした写真の中の人物の表情を分析して感情を推論し、それに合った雰囲気の音楽を推薦する機能です。
- **自然なチャットボットインタラクション:** 単純なコマンドではなく、日常的な対話を通じてユーザーと自然に相互作用し、音楽の推薦を提供します。

<br>

## 🖼️ プレビュー
<img width="425" height="931" alt="image" src="https://github.com/user-attachments/assets/5b2cbf48-3b80-4622-8f79-59db09d07c1c" />

<br>

## 🛠️ 使用技術

- **Backend:** `Java`, `Spring Boot`, `Spring Data JPA`
- **Mobile (Frontend):** `Kotlin (Android)`
- **Database:** `MySQL`
- **APIs & Others:** `OpenAI GPT API`, `YouTube API`
<br>

## ⚙️ インストール及び実行方法

```bash
本プロジェクトはAndroid環境で動作します。実行にはAndroid Studioが必要です。
また、Spring Boot(Backend)で駆動するため、正常なアプリの動作には必ずバックエンドサーバーを先に実行してください。

# 1. リポジトリのクローン (Clone)
git clone [https://github.com/Yun-Gi/SoundMate.git](https://github.com/Yun-Gi/SoundMate.git)

# 2. データベース設定 (MySQL)
MySQLを実行し、'SoundMate'スキーマを作成します。
SoundMateB/src/resources/application.properties ファイルで、DB接続情報を自身の環境に合わせて修正します。

# 3. バックエンドサーバーの実行
プロジェクト内の SoundMateB フォルダをIDEで開き、
src/main/java/.../Application.java ファイルを探して実行(Run)します。
注意: ローカル環境でテストする場合、Androidソースコード内のAPI通信IPアドレスを自身のPCのIPアドレス（例: 192.168.x.x）に変更する必要がある場合があります。

# 4. アプリケーションの実行
Android Studioを実行し、プロジェクトの SoundMate フォルダを開いてGradleの同期(Sync)が完了するまで待ちます。
その後、上部のツールバーでエミュレーターまたは接続されたデバイスを選択し、Runボタンを押します。
```

<br>

## 👨‍👩‍👧‍👦 チームメンバー紹介

| 名前 | 役割 |  
| イ・ユンギ | Androidアプリ実装 / DB設計およびAPI開発 |  
| チェ・グァンミン | デザイン案および全体UIフロー企画 |  
| ホン・ジョンウォン | 音楽推薦ロジック実装 / チャットボットメッセージ処理 |  


