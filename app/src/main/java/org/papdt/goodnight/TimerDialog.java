/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.papdt.goodnight;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import java.util.Calendar;
import java.util.Date;

/**
 * A dialog that prompts the user for the time of day using a {@link TimePicker}.
 *
 * <p>See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.</p>
 */
public class TimerDialog extends AlertDialog
        implements OnClickListener, OnTimeChangedListener {

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         */
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
        void onTimerCanceled();
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";

    private final TimePicker mTimePicker;
    private final TextView mTextView;
    private final OnTimeSetListener mCallback;

    private int mInitialHourOfDay = 0;
    private int mInitialMinute = 15;
    private boolean mIs24HourView;
    private boolean mHasSet = false;

    /**
     * @param context Parent.
     * @param callBack How parent is notified.
     * @param existingTimer Existing timer in unix time. -1 if not exist.
     */
    public TimerDialog(Context context,OnTimeSetListener callBack,
                                             long existingTimer) {

        this(context,0,callBack,existingTimer,true);
    }

    /**
     * @param context Parent.
     * @param theme the theme to apply to this dialog
     * @param callBack How parent is notified.
     * @param existingTimer Timer
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    private TimerDialog(Context context,
                        int theme,
                        OnTimeSetListener callBack,
                        long existingTimer, boolean is24HourView) {
        super(context, theme);
        mCallback = callBack;
        if(existingTimer != PlayerService.NULL){
            final Date d = new Date();
            d.setTime(existingTimer + d.getTimezoneOffset() * 60 * 1000);
            mInitialHourOfDay = d.getHours();
            mInitialMinute = d.getMinutes();
            mHasSet = true;
        }
        mIs24HourView = is24HourView;

        setIcon(0);
        setTitle(R.string.time_picker_dialog_title);

        Context themeContext = getContext();

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_picker_dialog, null);
        setView(view);
        mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
        mTextView = (TextView) view.findViewById(R.id.tv_time_picker);

        // initialize state
        mTimePicker.setIs24HourView(mIs24HourView);
        mTimePicker.setCurrentHour(mInitialHourOfDay);
        mTimePicker.setCurrentMinute(mInitialMinute);
        mTimePicker.setOnTimeChangedListener(this);
        if(mHasSet){
            setButton(BUTTON_POSITIVE, themeContext.getText(R.string.date_time_update), this);
            setButton(BUTTON_NEGATIVE, themeContext.getText(R.string.clear), this);
            if(mInitialHourOfDay != 0){
                mTextView.setText(themeContext
                        .getString(R.string.has_set_hour,mInitialHourOfDay,mInitialMinute));
            }else{
                mTextView.setText(themeContext.getString(R.string.has_set,mInitialMinute));
            }
        }else{
            setButton(BUTTON_POSITIVE, themeContext.getText(R.string.date_time_set),this);
        }

    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case BUTTON_POSITIVE:
                tryNotifyTimeSet();
                break;
            case BUTTON_NEGATIVE:
                tryNotifyTimerCanceled();
                break;
        }
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        /* do nothing */
    }

    private void tryNotifyTimeSet() {
        if (mCallback != null) {
            mTimePicker.clearFocus();
            mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute());
        }
    }

    private void tryNotifyTimerCanceled() {
        if (mCallback != null) {
            mTimePicker.clearFocus();
            mCallback.onTimerCanceled();
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mTimePicker.getCurrentHour());
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
    }
}