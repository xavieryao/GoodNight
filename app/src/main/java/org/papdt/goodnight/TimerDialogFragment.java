package org.papdt.goodnight;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Xavier on 14-6-28.
 */
public class TimerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private SharedPreferences mSettings;
    private Calendar mCal = Calendar.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the time already set as the default values for the picker
        mSettings = getActivity().getApplicationContext()
                .getSharedPreferences(MainActivity.PREFERENCE_TAG, Context.MODE_PRIVATE);
        long existingTimer = mSettings.getLong(MainActivity.PREF_TIMER,PlayerService.NULL);

        int hour = 0; // Default values
        int minute = 15;

        if(existingTimer != PlayerService.NULL){
            mCal.setTimeInMillis(existingTimer);
            hour = mCal.get(Calendar.HOUR);
            minute = mCal.get(Calendar.MINUTE);
        }
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        /*
        TODO cancel a timer.
         */
        mCal.clear();
        mCal.set(Calendar.HOUR_OF_DAY,hourOfDay);
        mCal.set(Calendar.MINUTE,minute);

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong(MainActivity.PREF_TIMER,mCal.getTimeInMillis());
        editor.commit();
    }
}