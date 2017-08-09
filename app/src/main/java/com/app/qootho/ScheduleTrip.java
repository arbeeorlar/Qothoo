package com.app.qootho;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.qootho.Utilities.DatePickerFragment;
import com.app.qootho.Utilities.Util;

import java.util.Calendar;

public class ScheduleTrip extends BottomSheetDialogFragment {

    public static final long FiveYearsInMillis = 157766400000L;
    static final int TIME_DIALOG_ID = 0;
    String mString;
    TextView txtDate;
    TextView txtTime;
    Button btnRequest;
    int expiryMonth;
    int expiryYear;
    private BottomSheetBehavior mBehavior;
    private DatePickerFragment expiryDateFragment;
    private int mHour;
    private int mMinute;
    private int mYear;
    private int mMonth;
    private int mDay;

    static ScheduleTrip newInstance(String string) {
        ScheduleTrip f = new ScheduleTrip();
        Bundle args = new Bundle();
        args.putString("string", string);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mString = getArguments().getString("string");

    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.activity_schedule_trip, null);
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        txtDate = (TextView) dialog.findViewById(R.id.txtDate);
        txtTime = (TextView) dialog.findViewById(R.id.txtTime);
        btnRequest = (Button) dialog.findViewById(R.id.btnRequestTrip);

        txtTime.setVisibility(View.GONE);
        btnRequest.setVisibility(View.GONE);


        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (expiryDateFragment == null) {
                    expiryDateFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            expiryMonth = monthOfYear + 1; //values start from 0, so add 1 to levelup
                            mYear = expiryYear = year;
                            mDay = dayOfMonth;
                            mMonth = monthOfYear;


                            txtDate.setText(Util.setCalenedar(year, monthOfYear, dayOfMonth)); //getString(R.string._card_expiry_format, expiryMonth, expiryYear));
                            txtTime.setVisibility(View.VISIBLE);
                        }
                    }, null, null, null);

                    expiryDateFragment.setCallback(new DatePickerFragment.DatePickerCallback() {
                        @Override
                        public void onCreateDialog(DatePickerDialog dialog) {
                            DatePicker picker = dialog.getDatePicker();
                            //minimum date should be today
                            picker.setMinDate(System.currentTimeMillis() - 10000); //subtract seconds to be safe
                            //max date shouldn't be more than 3-5 years from now. let's set five to be safe
                            picker.setMaxDate(System.currentTimeMillis() + FiveYearsInMillis);
                            dialog.setTitle(null);
                            expiryDateFragment.attemptDayVisibilityChangeInPicker(View.GONE);
                        }
                    });
                    expiryDateFragment.show(getFragmentManager(), "expiryDatePicker");
                } else {
                    try {
                        DatePickerDialog dialog = expiryDateFragment.getDatePickerDialog();
                        dialog.updateDate(expiryYear, expiryMonth, 0);
                        //do this to clear the title
                        dialog.setTitle(null);
                    } catch (Exception e) {

                    }
                    expiryDateFragment.getDatePickerDialog().show();
                }
            }
        });

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Get Current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mHour = hourOfDay;
                                mMinute = minute;
                                txtTime.setText(hourOfDay + ":" + minute);
                                btnRequest.setVisibility(View.VISIBLE);

                            }
                        }, hour, minute, true);
                timePickerDialog.show();

            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), BookRideActivity.class);
                Bundle b = new Bundle();
                b.putInt("month", mMonth);
                b.putInt("year", mYear);
                b.putInt("day", mDay);
                b.putInt("minute", mMinute);
                b.putInt("hour", mHour);
                b.putString("mString", mString);
                intent.putExtras(b);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        return dialog;
    }


    public interface MyDialogFragmentListener {
        void onReturnValue(String foo);
    }
}
