package com.example.daniel.lenarplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private static String SD_PATH =  Environment.getExternalStorageDirectory().getPath()+"/Muzyka";
    private List<String> songs = new ArrayList<>();
    private MediaPlayer mp = new MediaPlayer();
    private static String TAG="PlayerSuperApp";
    private int tmp_pos=-1;
    private MediaController mediaController = MainActivity.mediaController;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        
        IntentFilter iF_play = new IntentFilter();
        IntentFilter iF_pause = new IntentFilter();
        iF_pause.addAction("android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION");
        iF_play.addAction("android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION");

        registerReceiver(receiver_pause, iF_pause);
        registerReceiver(receiver_play, iF_play);

    }

    public void createNotification(String name,String song, int icon ){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        .setContentTitle(name)
                        .setContentText(song);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(0, mBuilder.build());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart");
        songs=intent.getExtras().getStringArrayList("songlist");
        int position = intent.getIntExtra("pos",0);
            //Toast.makeText(getApplicationContext(),Integer.toString(position), Toast.LENGTH_SHORT).show();
        Start_player(position);

        return START_NOT_STICKY;
    }

    void Start_player(int position){
        if(tmp_pos!=position){
            try{
                mp.reset();
                mp.setDataSource(SD_PATH + "/" + songs.get(position) + ".mp3");
                mp.prepare();
                mp.start();
                createNotification("MyPlayer", songs.get(position), android.R.drawable.ic_media_play);
                mediaController.setEnabled(true);
                mediaController.show();
                tmp_pos=position;
                //mp.prepareAsync();
            } catch (IllegalArgumentException | IllegalStateException | IOException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getClass().getName() + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            createNotification("MyPlayer", songs.get(position),android.R.drawable.ic_media_pause);
            mp.stop();
            tmp_pos=-1;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private  final BroadcastReceiver receiver_play = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "playReceive");
            //Toast.makeText(getApplicationContext(),"close session", Toast.LENGTH_SHORT).show();
            mp.start();
        }
    };
    private final BroadcastReceiver receiver_pause = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "pauseReceive");
            mp.pause();
        }
    };



}
