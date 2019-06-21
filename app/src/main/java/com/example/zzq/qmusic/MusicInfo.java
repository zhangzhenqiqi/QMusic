package com.example.zzq.qmusic;

public class MusicInfo {
    static long count=0;
    private long id;        //歌曲id
    private String name;    //音乐名称
    private String artist;  //作者
    private long time;      //音乐时长/s
    private String path;    //文件地址



    public MusicInfo(){
        super();
    }

    public MusicInfo(long id, String name, String artist, long time) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.time = time;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
