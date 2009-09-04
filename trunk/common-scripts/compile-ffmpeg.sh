#!/bin/bash

echo "Compiling opencore-amr ..."
git clone --depth=1 git://opencore-amr.git.sourceforge.net/gitroot/opencore-amr 
cd opencore-amr
make
sudo make install
make clean
echo "compiling opencore-amr done!"

echo "compiling ffmpeg ..."
svn checkout svn://svn.ffmpeg.org/ffmpeg/trunk ffmpeg
cd ffmpeg
./configure --prefix=/opt/ffmpeg
 
echo "compiling ffmpeg done!"