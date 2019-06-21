package com.example.zzq.qmusic;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.zzq.qmusic.MusicService.ORDER_PLAY;
import static com.example.zzq.qmusic.MusicService.SINGLE_PLAY;
import static com.example.zzq.qmusic.MusicService.RANDOM_PLAY;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {
    private TextView textView1_title;//歌名
    private SeekBar seekBar1;//进度条
    private TextView textView1_start_time,textView1_end_time,textView2_artist;//开始时间,结束时间
    private ImageView imageView1_play_mode;//菜单
    private ImageView imageView3_previous,imageView2_play_pause,imageView1_next;//上一首,播放暂停,下一首
    private ArrayList<MusicInfo> musicInfos;
    private boolean isPause = false;//当前播放的是否为暂停状态
    private static final int UPDATE_TIME = 0x1;//更新播放事件的标记
    private MusicService playService=null;
    private long position;//点击的歌曲位置
    private Thread thread;
    private ServiceConnection sc = new  ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = ((MusicService.PlayBinder)service).getPlayService();
            if(position>=0) playService.play((int) position); /***写在onCreate会导致playService为空从而产生异常，放在这里可以解决问题但是并不优雅= =***/
            else if (position==-1){
                if(playService.isPlaying()){
                    playService.updateUI();
                }else{
                    playService.play(0);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        //得到所点击歌曲的pos
        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);


        //注册广播
        MusicPlayerBroadcast musicPlayerBroadcast = new MusicPlayerBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MusicPlayerBroadcast");
        registerReceiver(musicPlayerBroadcast,intentFilter);

        //初始化界面信息
        textView1_title = (TextView) findViewById(R.id.textView1_title);//歌名
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);//进度条
        textView1_start_time = (TextView) findViewById(R.id.textView1_start_time);//开始时间
        textView1_end_time = (TextView) findViewById(R.id.textView1_end_time);//结束时间
        textView2_artist = findViewById(R.id.textView2_artist);
        imageView1_play_mode = (ImageView) findViewById(R.id.imageView1_play_mode);//菜单
        imageView3_previous = (ImageView) findViewById(R.id.imageView3_previous);//上一首
        imageView2_play_pause = (ImageView) findViewById(R.id.imageView2_play_pause);//播放暂停
        imageView1_next = (ImageView) findViewById(R.id.imageView1_next);//下一首

        //注册按钮点击监听事件
        imageView1_play_mode.setOnClickListener(this);
        imageView2_play_pause.setOnClickListener(this);
        imageView3_previous.setOnClickListener(this);
        imageView1_next.setOnClickListener(this);
        musicInfos = MusicUtil.getMusicData(this);
        seekBar1.setOnSeekBarChangeListener(this);
        bindPlayService();//绑定服务,异步过程,绑定后需要取得相应的值,来更新界面
        //myHandler = new MyHandler(this);
    }

    private void bindPlayService() {
        Intent intent =  new Intent(this,MusicService.class);
        bindService(intent,sc,BIND_AUTO_CREATE);
    }


    private boolean flag = false;
    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView2_play_pause: {//播放暂停按钮
                if (playService.isPlaying()) {//如果是播放状态
                    imageView2_play_pause.setImageResource(R.mipmap.app_music_play);//设置播放图片
                    playService.pause();
                    flag=true;
                }

                else {
                    if (playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.app_music_pause);//设置暂停图片
                        playService.start();//播放事件
                    } else {
                        //只有打开APP没有点击任何歌曲,同时,直接点击暂停播放按钮时.才会调用
                        playService.play(0);
                    }
                }
                break;
            }
            case R.id.imageView1_next:{//下一首按钮
                playService.next();//下一首
                break;
            }
            case R.id.imageView3_previous:{//上一首按钮
                playService.prev();//上一首
                break;
            }
            case R.id.imageView1_play_mode:{//循环模式按钮
                //int mode = (int) imageView1_play_mode.getTag();
                switch (playService.getPlay_mode()){
                    case ORDER_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.app_mode_random);
                        //imageView1_play_mode.setTag(RANDOM_PLAY);
                        playService.setPlay_mode(RANDOM_PLAY);
                        Toast.makeText(getApplicationContext(),"随机播放",Toast.LENGTH_SHORT).show();
                        break;
                    case RANDOM_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.app_mode_single);
                        //imageView1_play_mode.setTag(SINGLE_PLAY);
                        playService.setPlay_mode(SINGLE_PLAY);
                        Toast.makeText(getApplicationContext(),"单曲循环",Toast.LENGTH_SHORT).show();
                        break;
                    case SINGLE_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.app_mode_order);
                        //imageView1_play_mode.setTag(ORDER_PLAY);
                        playService.setPlay_mode(ORDER_PLAY);
                        Toast.makeText(getApplicationContext(),"顺序播放",Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            }
            default:
                break;
        }
    }

    private boolean isChange=false;
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            textView1_start_time.setText(MusicUtil.formatTime((long)((double)progress/100*playService.getDuration())));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
            isChange = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
            textView1_start_time.setText(MusicUtil.formatTime((long)((double)seekBar.getProgress()/100*playService.getDuration())));
            isChange = false;
    }


    class SeekBarThread implements Runnable {

        @Override
        public void run() {
            while (!isChange && playService.isPlaying()) {
                // 将SeekBar位置设置到当前播放位置
                seekBar1.setProgress(playService.getcurPos());
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                    //播放进度
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MusicPlayerBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            String artist = intent.getStringExtra("artist");
            String time = intent.getStringExtra("time");
            textView1_title.setText(name);
            textView1_end_time.setText(time);
            textView2_artist.setText(artist);
        }
    }


}
