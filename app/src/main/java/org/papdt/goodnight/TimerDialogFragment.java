package org.papdt.goodnight;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Xavier on 14-6-28.
 */
public class TimerDialogFragment extends DialogFragment implements TimerDialog.OnTimeSetListener {

    private final static String TAG = "TimerDialogFragment";

    private SharedPreferences mSettings;
    private Calendar mCal = Calendar.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the time already set as the default values for the picker
        mSettings = getActivity().getApplicationContext()
                .getSharedPreferences(MainActivity.PREFERENCE_TAG, Context.MODE_PRIVATE);
        long existingTimer = mSettings.getLong(MainActivity.PREF_TIMER,PlayerService.NULL);
        Log.d(TAG,"existing timer"+ existingTimer);
        // Create a new instance of TimerDialog and return it
        return new TimerDialog(getActivity(), this, existingTimer);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Date d = new Date(0);
        Log.d(TAG,"offset"+d.getTimezoneOffset());
        d.setHours(hourOfDay);
        d.setMinutes(minute - d.getTimezoneOffset());

        Log.d(TAG,"timer set." + d.getTime());

        long newTime = d.getTime();

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong(MainActivity.PREF_TIMER,newTime);
        editor.commit();
        notifyTimerChanged(newTime);
    }

    private void notifyTimerChanged(long newTimer) {
        Intent intent = new Intent(getActivity(),PlayerService.class);
        intent.setAction(PlayerService.ACTION_TIMER);
        intent.putExtra(MainActivity.PREF_TIMER,newTimer);
        getActivity().startService(intent);
    }

    @Override
    public void onTimerCanceled() {
        Log.d(TAG,"timer canceled.");
        SharedPreferences.Editor editor = mSettings.edit();
        editor.remove(MainActivity.PREF_TIMER);
        editor.commit();
        notifyTimerChanged(PlayerService.NULL);
    }
}