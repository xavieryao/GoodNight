package org.papdt.goodnight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Xavier on 14-6-28.
 */
public class ScreenStateReceiver extends BroadcastReceiver {

    private Intent mItScreenOn,mItScreenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_OFF)) {
            if(mItScreenOff == null){
                mItScreenOff = new Intent(context,PlayerService.class);
                mItScreenOff.setAction(Intent.ACTION_SCREEN_OFF);
            }
            context.startService(mItScreenOff);
        }else{
            if(mItScreenOn == null){
                mItScreenOn = new Intent(context,PlayerService.class);
                mItScreenOn.setAction(Intent.ACTION_SCREEN_ON);
            }
            context.startService(mItScreenOn);
        }
    }
}
