#!/bin/bash
#
mkdir live555
cd live555
wget http://www.live555.com/mediaServer/linux/live555MediaServer

echo "DOWNLOAD A SAMPLE MP3 FILE for Testing"
wget --output-document=test.mp3 http://www.a1sounddownload.com/Halloween123/ghoullaugh.mp3
sudo chmod +x live555MediaServer

echo "Execute the Live555MediaServer"
live555MediaServer
