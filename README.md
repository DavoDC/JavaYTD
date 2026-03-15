# JavaYTD

A Java Swing GUI front-end for [youtube-dl](https://github.com/ytdl-org/youtube-dl) — download YouTube videos and audio without touching the command line.

![Preview](https://github.com/DavoDC/JavaYTD/blob/master/Preview.png)

## Features
- Paste a YouTube URL and download with one click
- Choose download format (best quality, audio-only, etc.)
- Downloads saved directly to your Downloads folder
- Built-in progress output so you can see what's happening
- Requires `youtube-dl.exe` placed alongside the application

## Setup
1. Download [youtube-dl.exe](https://github.com/ytdl-org/youtube-dl/releases) and place it in the same folder as the JavaYTD executable
2. Run the executable from `Executable/`
3. Paste a URL, pick a format, and download

## Tech
- Java (Swing / Nimbus L&F)
- Wraps youtube-dl.exe via process execution
- Built with Launch4J for Windows executable packaging
