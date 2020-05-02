package game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;

public class VideoPlayer {
    private ArrayList<String> videos; // holds all video links
    private Media media;
    private MediaPlayer mediaPlayer;
    private String path;
    private int index;
    private int numTimesPlayed;

    public VideoPlayer(){
        videos = new ArrayList<String>();
        String folderPath = "src\\data\\test_videos";
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            for (File f: folder.listFiles()) {
                String s = f.getPath();
                String t = f.toURI().toString();
                videos.add(t);
            }
        }
        this.path = videos.get(0);
        this.media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        numTimesPlayed = 0;
    }

    /*
     * returns true if video player hasn't run out of videos to load
     */
    public boolean loadNextVideo(){
        numTimesPlayed = 0;
        index++;
        if (index >= videos.size()){
            return false;
        }
        this.path = videos.get(index);
        this.media = new Media(path);
        this.mediaPlayer = new MediaPlayer(media);
        return true;
    }

    public void enableAutoPlay(){ mediaPlayer.setAutoPlay(true);
    }

    public void play(){
        numTimesPlayed++;
        mediaPlayer.play();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void restart(){
        numTimesPlayed++;
        mediaPlayer.seek(mediaPlayer.getStartTime());
        mediaPlayer.play();
    }

    public boolean onEndOfVideo(){
        return (mediaPlayer.getCurrentTime().equals(mediaPlayer.getTotalDuration()));
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void loadVideo(String path){
        this.media = new Media(path);
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    public int getNumTimesPlayed(){ return numTimesPlayed; }

}
