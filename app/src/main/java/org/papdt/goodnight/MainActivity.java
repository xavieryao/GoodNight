package org.papdt.goodnight;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mIvPlay = (ImageView) findViewById(R.id.iv_play);
        mIvPlay.setOnClickListener(this);
        mReceiver = new PlayStatusReceiver();
        IntentFilter filter = new IntentFilter(PlayerService.ACTION_PLAYING);
        registerReceiver(mReceiver,filter);
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
                if(mItPause == null){
                    mItPause = new Intent(this,PlayerService.class);
                    mItPause.setAction(PlayerService.ACTION_PAUSE);
                }
                startService(mItPause);
                mIsPlaying = false;
                mIvPlay.setImageResource(R.drawable.play);
                Log.d(TAG,"startService:pause");
            }else{ //Play
                if(mItPlay == null){
                    mItPlay = new Intent(this,PlayerService.class);
                    mItPlay.setAction(PlayerService.ACTION_PLAY);
                }
                startService(mItPlay);
                mIsPlaying = true;
                mIvPlay.setImageResource(R.drawable.pause);
                Log.d(TAG,"startService:play");
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

    private class PlayStatusReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //Received broadcast: now playing.
            if(intent.getAction().equals(PlayerService.ACTION_PLAYING) && !mIsPlaying) {// If necessary, set button image to pause.
                mIvPlay.setImageResource(R.drawable.pause);
                mIsPlaying = true;
            }else if(intent.getAction().equals(PlayerService.ACTION_PAUSED) && mIsPlaying){
                mIvPlay.setImageResource(R.drawable.play);
                mIsPlaying = false;
            }
        }
    }
}
