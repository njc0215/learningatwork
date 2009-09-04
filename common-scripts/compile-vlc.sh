#!/bin/bash
#
#Support: http://wiki.videolan.org/UnixCompile
#Download VLC source code from http://www.videolan.org/vlc/download-sources.html
wget http://download.videolan.org/pub/videolan/vlc/1.0.1/vlc-1.0.1.tar.bz2

echo "Then extract vlc"
tar -xf vlc-1.0.1.tar.bz2
cd vlc-1.0.1.tar.bz2

apt-get build-dep vlc

#can be modified as your requires
./configure --enable-x11 --enable-xvideo --enable-sdl --enable-avcodec --enable-avformat --enable-swscale --enable-mad --enable-libdvbpsi --enable-a52 --enable-libmpeg2 --enable-dvdnav --enable-faad --enable-vorbis --enable-ogg --enable-theora --enable-faac --enable-mkv --enable-freetype --enable-fribidi --enable-speex --enable-flac --enable-caca --enable-skins --enable-skins2 --enable-alsa --disable-kde --enable-qt4 --enable-ncurses --enable-release --enable-libarm --prefix=/opt/vlc

make

sudo make install

make clean

