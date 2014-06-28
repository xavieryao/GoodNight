package org.papdt.goodnight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Xavier on 14-6-27.
 */
public class NoisyReceiver extends BroadcastReceiver {
    private Intent mIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mIntent == null){
            mIntent = new Intent(context,PlayerService.class);
            mIntent.setAction(PlayerService.ACTION_PAUSE);
        }
        context.startService(mIntent);
    }
}
