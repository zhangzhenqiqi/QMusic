package com.example.zzq.qmusic;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicService extends Service  implements MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener {
    public MediaPlayer mediaPlayer;
    public int curPos;
    ArrayList<MusicInfo> musicInfos;


    //播放模式
    public static final int ORDER_PLAY = 1;//顺序播放
    public static final int RANDOM_PLAY = 2;//随机播放
    public static final int SINGLE_PLAY = 3;//单曲循环
    private int play_mode = ORDER_PLAY;//播放模式,默认为顺序播放

    private PlayBinder playBinder = new PlayBinder();
    //set方法
    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    //get方法
    public int getPlay_mode() {
        return play_mode;
    }

    private boolean isPause = false;//歌曲播放中的暂停状态

    public boolean isPause(){
        return isPause;
    }

    //获取当前是否为播放状态,提供按钮点击事件判断状态时调用
    public boolean isPlaying(){
        if (mediaPlayer!=null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }


    public MusicService(){

    }

    public int getcurPos(){
        return curPos;
    }

    private Random random = new Random();//创建随机对象


    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (play_mode){
            case ORDER_PLAY://顺序播放
                next();//下一首
                break;
            case RANDOM_PLAY://随机播放
                play(random.nextInt(musicInfos.size()));
                break;
            case SINGLE_PLAY://单曲循环
                play(curPos);
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();//重启
        return false;
    }

    //内部类PlayBinder实现Binder,得到当前PlayService对象
    public class PlayBinder extends Binder {

        public MusicService getPlayService(){
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return playBinder;//通过PlayBinder拿到PlayService,给Activity调用
    }

    @Override
    public void onCreate() {
        //super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);//注册播放完成事件
        mediaPlayer.setOnErrorListener(this);//注册播放错误事件
        musicInfos = MusicUtil.getMusicData(this);//获取Mp3列表
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void updateUI(){
        if (curPos>=0 && curPos<musicInfos.size()){
            MusicInfo musicInfo = musicInfos.get(curPos);//获取mp3Info对象
                /**
                 * 利用广播更新UI
                 * */
                Intent intent = new Intent("MusicPlayerBroadcast");
                intent.putExtra("name",musicInfo.getName());
                intent.putExtra("time",MusicUtil.formatTime(musicInfo.getTime()));
                intent.putExtra("artist",musicInfo.getArtist());
                sendBroadcast(intent);

        }
    }

    //播放
    public void play(int position){
        if (position>=0 && position<musicInfos.size()){
            MusicInfo musicInfo = musicInfos.get(position);//获取mp3Info对象
            //进行播放,播放前判断
            try {
                mediaPlayer.reset();//重启
                mediaPlayer.setDataSource(this, Uri.parse(musicInfo.getPath()));//资源解析,Mp3地址
                mediaPlayer.prepare();//准备
                mediaPlayer.start();//开始(播放)
                curPos = position;//保存当前位置到curPos,比如第一首,curPos = 1


                /**
                 * 利用广播更新UI
                 * */
               updateUI();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    //暂停
    public void pause(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause = true;
        }
    }

    //下一首
    public void next(){
        switch (play_mode){
            case ORDER_PLAY://顺序播放
                if (curPos>=musicInfos.size()-1){//如果超出最大值,(因为第一首是0),说明已经是最后一首
                    curPos = 0;//回到第一首
                }else {
                    curPos++;//下一首
                }
                play(curPos);//下一首
                break;
            case RANDOM_PLAY://随机播放
                play(random.nextInt(musicInfos.size()));
                break;
            case SINGLE_PLAY://单曲循环
                play(curPos);
                break;
            default:
                break;
        }
    }

    //上一首 previous
    public void prev(){
        if (curPos-1<0){//如果上一首小于0,说明已经是第一首
            curPos = musicInfos.size()-1;//回到最后一首
        }else {
            curPos--;//上一首
        }
        play(curPos);
    }

    //默认开始播放的方法
    public void start(){
        if (mediaPlayer!=null && !mediaPlayer.isPlaying()){//判断当前歌曲不等于空,并且没有在播放的状态
            mediaPlayer.start();
        }
    }


    //获取当前的进度值
    public int getCurrentProgress(){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){//mediaPlayer不为空,并且,为播放状态
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    //getDuration 获取文件的持续时间
    public int getDuration(){
        return mediaPlayer.getDuration();
    }
    //seekTo 寻找指定的时间位置
    public void seekTo(int msec){
        mediaPlayer.seekTo(msec);
    }



}
