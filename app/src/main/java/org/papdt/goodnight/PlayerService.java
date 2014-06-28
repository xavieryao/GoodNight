package org.papdt.goodnight;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.lang.Override;

public class PlayerService extends Service implements MediaPlayer.OnErrorListener{

    public static final String TAG = "PlayerService";
    public static final int NOTIFICATION_ID = 970809;
    public static final long NULL = -1l;

    public static final String ACTION_PLAY = "org.papdt.goodnight.action_play";
    public static final String ACTION_PAUSE = "org.papdt.goodnight.action_pause";
    public static final String ACTION_PLAYING = "org.papdt.goodnight.action_playing";
    public static final String ACTION_PAUSED = "org.papdt.goodnight.action_paused";
    public static final String ACTION_TIMER = "org.papdt.goodnight.action_timer";

    private MediaPlayer mPlayer;
    private Notification mNotification;
    private Intent mItPaused,mItPlaying;
    private ScreenStateReceiver mReceiver;
    private long mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.rainymood);
        mPlayer.setLooping(true);

        MessageSenderThread t = new MessageSenderThread();
        t.start();

        mTimer = getSharedPreferences(MainActivity.PREFERENCE_TAG,MODE_PRIVATE)
                .getLong(MainActivity.PREF_TIMER,NULL);

        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return START_STICKY;
        String action = intent.getAction();
        if(action.equals(ACTION_PLAY)){
            Log.d(TAG,"play action received");
            play();
        }else if (action.equals(ACTION_PAUSE)){
            Log.d(TAG,"pause action received");
            pause();
        }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
            //Screen off
        }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            //Screen on
        }else if(action.equals(ACTION_TIMER)){
            //Set timer
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mPlayer.release();
        mPlayer = null;
        if(mReceiver !=null){
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent i) {
        // TODO Auto-generated method stub
        return null;
    }

    private void play(){
        mPlayer.start();
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //Foreground
        if(mNotification == null){
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            CharSequence str = getText(R.string.enjoy);
            mNotification = new Notification.Builder(getApplication())
                    .setTicker(str)
                    .setContentIntent(pi)
                    .setOngoing(true)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(str)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .getNotification();
        }
        startForeground(NOTIFICATION_ID, mNotification);
        //Register receiver
        if(mReceiver == null){
            mReceiver = new ScreenStateReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(mReceiver,filter);
        }
    }

    private void pause(){
        mPlayer.pause();
        stopForeground(true);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.rainymood);
        mPlayer.setLooping(true);
        return true;
    }

    private class MessageSenderThread extends Thread{
        private PowerManager mPm = (PowerManager) getSystemService(POWER_SERVICE);

        @Override
        public void run(){
            while(true){
                if(mPm.isScreenOn()){
                    if(mPlayer.isPlaying()){
                        if(mItPlaying == null){
                            mItPlaying = new Intent(ACTION_PLAYING);
                        }
                        sendBroadcast(mItPlaying);
                    }else{
                        if(mItPaused == null){
                            mItPaused = new Intent(ACTION_PAUSED);
                        }
                        sendBroadcast(mItPaused);
                    }
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
