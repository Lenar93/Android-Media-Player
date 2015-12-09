package com.example.daniel.lenarplayer;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Mp3Filter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String filename) {
        return (filename.endsWith(".mp3"));
    }
}
public class MainActivity extends ListActivity implements OnPreparedListener, MediaController.MediaPlayerControl{

    private List<String> songs = new ArrayList<String>();
    private MediaPlayer mp;
    private MediaController mediaController;
    private static String SD_PATH =  Environment.getExternalStorageDirectory().getPath()+"/Muzyka";
    private static String TAG="PlayerSuperApp";
    private Handler handler = new Handler();
    private int pos=-1;
    private boolean start_pause=true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = new MediaPlayer();
        mp.setOnPreparedListener(this);
        mediaController = new MediaController(this);
        updatePlaylist();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (songs.size() - 1 > pos && pos >= 0) {
                    playSong(++pos, 0);
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        pos=position;
        playSong(position,0);
        super.onListItemClick(l, v, position, id);
    }

    private void updatePlaylist() {
        File home = new File(SD_PATH);
        if(home.listFiles(new Mp3Filter()).length>0){
            for(File file : home.listFiles(new Mp3Filter())){
                songs.add(file.getName().substring(0,file.getName().length()-4));
            }
            ArrayAdapter<String> songlist = new ArrayAdapter<String>(this,R.layout.list_row,songs);
            setListAdapter(songlist);
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared");
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.mediaController));

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }
    private void playSong(int pos,int time){
        try{
            mp.reset();
            mp.setDataSource(SD_PATH + "/" + songs.get(pos) + ".mp3");
            mp.prepare();
            mp.seekTo(time);
            mp.start();

        }catch (IOException e){
            Log.d(TAG,"Błąd podczas otwierania pliku");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        //mediaController.setEnabled(true);
        mediaController.show();
        return false;
    }

    @Override
    public void start() {
        mp.start();
    }

    @Override
    public void pause() {
        mp.pause();
    }

    @Override
    public int getDuration() {
        //Toast.makeText(getApplicationContext(), Integer.toString(mp.getCurrentPosition()), Toast.LENGTH_SHORT).show();
        return mp.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mp.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mp.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mp.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mp.getAudioSessionId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Onresume");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mp.getCurrentPosition());
        outState.putInt("song", pos);
        outState.putBoolean("playing", mp.isPlaying());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        if(state.getBoolean("playing")&& start_pause){
            pos=state.getInt("song");
            playSong(pos,state.getInt("position"));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mp.getCurrentPosition()!=mp.getDuration() && start_pause)
            mp.start();
        Log.d(TAG, "Start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "Restart");
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(start_pause)
            mp.pause();
        Log.d(TAG, "Stop");
    }

}
