package org.papdt.goodnight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    public static final String PREFERENCE_TAG = "GOODNIGHT_PREF";
    public static final String PREF_TIMER = "PREF_TIMER";

    private ImageView mIvPlay;
    private boolean mIsPlaying;
    private PlayStatusReceiver mReceiver;
    private Intent mItPlay;
    private Intent mItPause;
    private AlertDialog mDialogWarn;
    private AudioManager mAudioManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mIvPlay = (ImageView) findViewById(R.id.iv_play);
        mIvPlay.setOnClickListener(this);
        mReceiver = new PlayStatusReceiver();
        IntentFilter filter = new IntentFilter(PlayerService.ACTION_ANSWER_QUERY);
        registerReceiver(mReceiver,filter);

        Intent query = new Intent(this,PlayerService.class);
        query.setAction(PlayerService.ACTION_QUERY_STATE);
        startService(query);
	}

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_play){
            if(mIsPlaying){ // Now Playing. Click to pause.
                onPauseClicked();
            }else{ //Play
                onPlayClicked();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_info:
                AboutDialog dialog = new AboutDialog();
                dialog.show(getFragmentManager(),"About");
                return true;
            case R.id.menu_timer:
                pickTime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pickTime() {
        TimerDialogFragment dialog = new TimerDialogFragment();
        dialog.show(getFragmentManager(),"TimerDialog");
    }

    private void onPlayClicked(){
        if(mItPlay == null){
            mItPlay = new Intent(this,PlayerService.class);
            mItPlay.setAction(PlayerService.ACTION_PLAY);
        }
        startService(mItPlay);
        mIsPlaying = true;
        mIvPlay.setImageResource(R.drawable.pause);
        if(mAudioManager == null){
            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        if(mAudioManager.isWiredHeadsetOn() && mDialogWarn == null){
            mDialogWarn = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            mDialogWarn.show();
        }
        Log.d(TAG,"startService:play");
    }

    private void onPauseClicked(){
        if(mItPause == null){
            mItPause = new Intent(this,PlayerService.class);
            mItPause.setAction(PlayerService.ACTION_PAUSE);
        }
        startService(mItPause);
        mIsPlaying = false;
        mIvPlay.setImageResource(R.drawable.play);
        Log.d(TAG,"startService:pause");
    }

    private class PlayStatusReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //Received broadcast: now playing.
            if(intent.getBooleanExtra(PlayerService.EXTRA_STATE,false)) {// Already playing.
                mIvPlay.setImageResource(R.drawable.pause);
                mIsPlaying = true;
            }else{
                mIvPlay.setImageResource(R.drawable.play);
                mIsPlaying = false;
            }
        }
    }
}
