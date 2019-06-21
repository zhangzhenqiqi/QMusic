package com.example.zzq.qmusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MusicListAdapter extends BaseAdapter {

    private Context context;
    private List<MusicInfo> musicInfos;
    private  MusicInfo musicInfo;

    public MusicListAdapter(Context context, List<MusicInfo> musicInfos) {
        this.context = context;
        this.musicInfos = musicInfos;
    }

    @Override
    public int getCount() {
        return musicInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 关键的函数getView()在每个子项被滚动到屏幕内的时候进行调用
     * 在这里面布置好布局后返回这个布局
     *
     * 一个优化是使用ViewHolder进行数据缓存，当convertView为空的时候，将这个布局缓存进viewholder中，
     * 不为空的话重新获得viewholder即可
     * viewholder+setTag&getTag 的作用在于，将布局绑定一个viewholder，如果convertview不为空直接用viewHolder设置布局
     * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder =null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.music_item,null);
            viewHolder = new ViewHolder();
            viewHolder.artist = convertView.findViewById(R.id.artist);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.time = convertView.findViewById(R.id.time);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        musicInfo = musicInfos.get(position);
        viewHolder.name.setText(musicInfo.getName());
        viewHolder.artist.setText(musicInfo.getArtist());
        viewHolder.time.setText(MusicUtil.formatTime(musicInfo.getTime()));
        return convertView;
    }

    /**
     * 声明ViewHolder，实现对实例的缓存，提升性能
     * */
    public class ViewHolder{
        public TextView name;
        public TextView time;
        public TextView artist;
    }
}
