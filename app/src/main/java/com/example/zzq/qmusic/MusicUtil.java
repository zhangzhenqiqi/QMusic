package com.example.zzq.qmusic;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MusicUtil {


        /*
        * 扫描本地的music
        * */
        public static ArrayList<MusicInfo> getMusicData(Context context){
            long cur=0;
            ArrayList<MusicInfo> musicInfoList = new ArrayList<MusicInfo>();
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, MediaStore.Audio.AudioColumns.IS_MUSIC);
            if(cursor!=null){
                while(cursor.moveToNext()){
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                    musicInfo.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                    musicInfo.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    musicInfo.setTime(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                    musicInfo.setId(++cur);
                    musicInfoList.add(musicInfo);
                }
                cursor.close();
            }
            MusicInfo.count=cur;
            return musicInfoList;
        }


        /**
         * 将歌曲时长(ms)转化为h:m:s的形式
         * */
        public static String formatTime(long time){
            time/=1000;
            long h=time/3600; time=time-h*3600;
            long m=time/60;   time=time-m*60;
            long s=time;
            String sh = (h<=9?"0":"")+h;
            String sm = (m<=9?"0":"")+m;
            String ss = (s<=9?"0":"")+s;
            return sh+":"+sm+":"+ss;
        }
}
