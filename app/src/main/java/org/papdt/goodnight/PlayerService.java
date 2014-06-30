package org.papdt.goodnight;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import java.lang.Override;

public class PlayerService extends Service implements MediaPlayer.OnErrorListener{

    public static final String TAG = "PlayerService";
    public static final int NOTIFICATION_ID = 970809;
    public static final int REQUEST_CODE = 970809;
    public static final long NULL = -1l;
/*
 * Actions
 */
    public static final String ACTION_PLAY = "org.papdt.goodnight.action_play";
    public static final String ACTION_PAUSE = "org.papdt.goodnight.action_pause";
    public static final String ACTION_STOP = "org.papdt.goodnight.action_stop";
    public static final String ACTION_TIMER = "org.papdt.goodnight.action_timer";
    public static final String ACTION_QUERY_STATE = "org.papdt.goodnight.action_query_state";
    public static final String ACTION_ANSWER_QUERY = "org.papdt.goodnight.action_answer_query";

/*
 * Extra Key
 */
    public static final String EXTRA_STATE = "org.papdt.goodnight.extra_is_playing";
    private MediaPlayer mPlayer;
    private Notification mNotification;
    private ScreenStateReceiver mReceiver;
    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;
    private long mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.rainymood);
        mPlayer.setLooping(true);
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

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
        }else if(action.equals(ACTION_STOP)){
            Log.d(TAG,"stop action received");
            stop();
        }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
            //Screen off
            Log.d(TAG,"Screen turned off.");
            if(mTimer != NULL){
                switchOnTimer();
            }
        }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            Log.d(TAG,"Screen turned on.");
            if(mTimer != NULL){
                switchOffTimer();
            }
        }else if(action.equals(ACTION_TIMER)){
            mTimer = intent.getLongExtra(MainActivity.PREF_TIMER,NULL);
            Log.d(TAG,"timer changed to "+mTimer);
        }else if(action.equals(ACTION_QUERY_STATE)){
            Log.d(TAG,"query received.");
            answerStateQuery();
        }

        return START_STICKY;
    }

    private void answerStateQuery() {
        Intent i = new Intent(ACTION_ANSWER_QUERY);
        boolean isPlaying = mPlayer.isPlaying();
        Log.d(TAG,"answer query:"+ isPlaying);
        i.putExtra(EXTRA_STATE,isPlaying);
        sendBroadcast(i);
    }

    private void switchOnTimer() {
        if(mPendingIntent == null){
            Intent i = new Intent(getApplicationContext(),PlayerService.class);
            i.setAction(ACTION_STOP);
            mPendingIntent = PendingIntent
                    .getService(getApplicationContext()
                            , REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        Log.d(TAG,"Timer is on.");
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                  mTimer + SystemClock.elapsedRealtime(),mPendingIntent);
    }

    private void switchOffTimer() {
        Log.d(TAG,"Timer canceled.");
        mAlarmManager.cancel(mPendingIntent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stop();
    }

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    private void play(){
        mPlayer.start();
        //Foreground
        if(mNotification == null){
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
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
        if(mTimer == NULL){
            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCE_TAG,MODE_PRIVATE);
            mTimer = prefs.getLong(MainActivity.PREF_TIMER,NULL);
        }
    }

    private void pause(){
        mPlayer.pause();
        stopForeground(true);
    }

    private void stop(){
        if(mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
        if(mReceiver !=null){
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.rainymood);
        mPlayer.setLooping(true);
        return true;
    }


}
