package com.example.daniel.lenarplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

class Mp3Filter implements FilenameFilter {//filtr plików mp3
    @Override
    public boolean accept(File dir, String filename) {
        return (filename.endsWith(".mp3"));
    }
}
public class MainActivity extends ListActivity{

    private List<String> songs = new ArrayList<>();
    public static MediaController mediaController;
    Intent serviceIntent;
    private static String SD_PATH =  Environment.getExternalStorageDirectory().getPath()+"/Muzyka";
    private static String TAG="PlayerSuperApp";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaController=new MediaController(this);
        //create service intent
        try{
            serviceIntent = new Intent(this, MusicService.class);
            initViews();
            Log.d(TAG, "onServiceCreate");
            //setListhers();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getClass().getName()+" "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {

        updatePlaylist();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //pos=position;
        //playSong(position,0);
        serviceIntent.putStringArrayListExtra("songlist", (ArrayList<String>) songs);
        serviceIntent.putExtra("pos", position);
        Log.d(TAG, "onClickListItem");

        try{
            startService(serviceIntent);
            Log.d(TAG, "onStartService");
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getClass().getName()+" "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        //super.onListItemClick(l, v, position, id);
    }




    private void updatePlaylist() {
        Log.d(TAG, "updatePlaylist");
        File home = new File(SD_PATH);
        if(home.listFiles(new Mp3Filter()).length>0){
            for(File file : home.listFiles(new Mp3Filter())){
                songs.add(file.getName().substring(0,file.getName().length()-4));
            }
            ArrayAdapter<String> songlist = new ArrayAdapter<>(this,R.layout.list_row,songs);
            setListAdapter(songlist);
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
