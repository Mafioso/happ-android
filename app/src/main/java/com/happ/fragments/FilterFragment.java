package com.happ.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.happ.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dante on 9/27/16.
 */
public class FilterFragment extends DialogFragment {


    private Button mStartDate, mEndDate;
    private EditText mStartDateText, mEndDateText;
    private SwitchCompat mFilterFree;
    private Date startDate, endDate;
    private DateFilterAppliedListener dateFilterAppliedListener;
    private String isFree = "";
    public FilterFragment() {

    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    public interface DateFilterAppliedListener {
        void onDateFilterApplied(Date startDate, Date endDate, String isFree);
    }

    public void setOnDateFilterAppliedListener(DateFilterAppliedListener listener) {
        this.dateFilterAppliedListener = listener;
    }

    public void setState(Date startDate, Date endDate, String isFree) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isFree = isFree;
    }

    @Override
    public void onResume() {
        super.onResume();
        java.text.DateFormat format = DateFormat.getLongDateFormat(getActivity());
        mStartDateText.setText(format.format(startDate));
        mEndDateText.setText(format.format(endDate));
        if (isFree != null && isFree.length() > 0) {
            mFilterFree.setChecked(true);
        } else {
            mFilterFree.setChecked(false);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.filter_fragment, null);
        final Activity activity = getActivity();

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.filter_feed)
                .setView(contentView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mFilterFree.isChecked()) {
                            isFree = "0";
                        } else {
                            isFree = "";
                        }
                            dateFilterAppliedListener.onDateFilterApplied(startDate, endDate, isFree);
                    }

                })
                .create();

        mStartDate = (Button) contentView.findViewById(R.id.filter_btn_start_date);
        mEndDate = (Button) contentView.findViewById(R.id.filter_btn_end_date);
        mStartDateText = (EditText) contentView.findViewById(R.id.filter_input_start_date);
        mEndDateText = (EditText) contentView.findViewById(R.id.filter_input_end_date);
        mFilterFree = (SwitchCompat) contentView.findViewById(R.id.filter_free);

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate = new Date();

                DatePickerDialog dpdstart = DatePickerDialog.newInstance(
                        createStartDateListener(),
                        startDate.getYear(),
                        startDate.getMonth()-1,
                        startDate.getDay()
                );
                dpdstart.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dpdstart.show(getActivity().getFragmentManager(), "StartDatepickerdialog");

            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDate = new Date();

                DatePickerDialog dpdend = DatePickerDialog.newInstance(
                        createEndDateListener(),
                        endDate.getYear(),
                        endDate.getMonth()-1,
                        endDate.getDay()
                );
                dpdend.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dpdend.show(getActivity().getFragmentManager(), "EndDatepickerdialog");

            }
        });

        return dialog;
    }


    private DatePickerDialog.OnDateSetListener createEndDateListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                if (endDate == null) endDate = new Date();
                calendar.setTime(endDate);
                calendar.set(year, monthOfYear, dayOfMonth);

                endDate = calendar.getTime();
                java.text.DateFormat format = DateFormat.getLongDateFormat(getActivity());

                mEndDateText.setText(format.format(endDate));

            }
        };
    }

    private DatePickerDialog.OnDateSetListener createStartDateListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                if (startDate == null) startDate = new Date();
                calendar.setTime(startDate);
                calendar.set(year, monthOfYear, dayOfMonth);

                startDate = calendar.getTime();
                java.text.DateFormat format = DateFormat.getLongDateFormat(getActivity());
                mStartDateText.setText(format.format(startDate));
            }
        };
    }

}
