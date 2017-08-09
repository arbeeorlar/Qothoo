package com.app.qootho.Utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * Created by abeeorlar on 4/29/2016.
 */
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private Integer mInitYear;
    private Integer mInitMonth;
    private Integer mInitDay;

    private DatePickerDialog dialog;

    private DatePickerCallback callback;

    public DatePickerFragment() {

    }

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener,
                                                 Integer initialYear, Integer initialMonth, Integer initialDay) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.listener = listener;
        datePickerFragment.mInitYear = initialYear;
        datePickerFragment.mInitMonth = initialMonth;
        datePickerFragment.mInitDay = initialDay;

        return datePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (dialog != null)
            return dialog;

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = mInitYear == null || mInitYear < 0 ? c.get(Calendar.YEAR) : mInitYear;
        int month = mInitMonth == null || mInitMonth < 0 ? c.get(Calendar.MONTH) : mInitMonth;
        int day = mInitDay == null || mInitDay < 0 ? c.get(Calendar.DAY_OF_MONTH) : mInitDay;

        // Create a new instance of DatePickerDialog and return it
        dialog = new DatePickerDialog(getActivity(), listener, year, month, day);

        if (callback != null)
            callback.onCreateDialog(dialog);

        return dialog;
    }

    public void setCallback(DatePickerCallback callback) {
        this.callback = callback;
    }

    public DatePickerDialog getDatePickerDialog() {
        return dialog;
    }

    public void attemptYearVisibilityChangeInPicker(int visibility) {
        try {
            DatePicker dp_mes = dialog.getDatePicker();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int spinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
                if (spinnerId != 0) {
                    View spinner = dp_mes.findViewById(spinnerId);
                    if (spinner != null) {
                        spinner.setVisibility(visibility);
                    }
                }
            } else {
                Field f[] = dp_mes.getClass().getDeclaredFields();
                for (Field field : f) {
                    if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) {
                        field.setAccessible(true);
                        Object picker = null;
                        try {
                            picker = field.get(dp_mes);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        ((View) picker).setVisibility(visibility);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void attemptMonthVisibilityChangeInPicker(int visibility) {
        try {
            DatePicker dp_mes = dialog.getDatePicker();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int spinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
                if (spinnerId != 0) {
                    View spinner = dp_mes.findViewById(spinnerId);
                    if (spinner != null) {
                        spinner.setVisibility(visibility);
                    }
                }
            } else {
                Field f[] = dp_mes.getClass().getDeclaredFields();
                for (Field field : f) {
                    if (field.getName().equals("mMonthPicker") || field.getName().equals("mMonthSpinner")) {
                        field.setAccessible(true);
                        Object picker = null;
                        try {
                            picker = field.get(dp_mes);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        ((View) picker).setVisibility(visibility);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void attemptDayVisibilityChangeInPicker(int visibility) {
        try {
            DatePicker dp_mes = dialog.getDatePicker();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int spinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (spinnerId != 0) {
                    View spinner = dp_mes.findViewById(spinnerId);
                    if (spinner != null) {
                        spinner.setVisibility(visibility);
                    }
                }
            } else {
                Field f[] = dp_mes.getClass().getDeclaredFields();
                for (Field field : f) {
                    if (field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner")) {
                        field.setAccessible(true);
                        Object picker = null;
                        try {
                            picker = field.get(dp_mes);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        ((View) picker).setVisibility(visibility);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public interface DatePickerCallback {
        void onCreateDialog(DatePickerDialog dialog);
    }
}
