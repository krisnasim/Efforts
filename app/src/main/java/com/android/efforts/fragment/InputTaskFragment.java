package com.android.efforts.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.efforts.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputTaskFragment extends Fragment {

    @BindView(R.id.task_title_input) EditText task_title_input;
    @BindView(R.id.task_content_input) EditText task_content_input;
    @BindView(R.id.start_date_task_input) EditText start_date_task_input;
    @BindView(R.id.end_date_task_input) EditText end_date_task_input;
    @BindView(R.id.task_status_type) Spinner task_status_type;
    @BindView(R.id.submit_task_btn) Button submit_task_btn;

    private Calendar myCalendar;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;

    @OnClick(R.id.submit_task_btn)
    public void sendTask() {
        String tasktitle = task_title_input.getText().toString();
        String taskContent = task_content_input.getText().toString();
        String taskStartDate = start_date_task_input.getText().toString();
        String taskEndDate = end_date_task_input.getText().toString();
        String taskStatus = task_status_type.getSelectedItem().toString();

        //show the loading dialog
        submit_task_btn.setEnabled(false);
        progressDialog = new ProgressDialog(getActivity(), R.style.CustomDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
    }

    @OnClick(R.id.start_date_task_input)
    public void pickStartDate() {
        new DatePickerDialog(getActivity(), startDateListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        updateDateLabel(start_date_task_input);
    }

    @OnClick(R.id.end_date_task_input)
    public void pickEndDate() {
        new DatePickerDialog(getActivity(), endDateListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        updateDateLabel(end_date_task_input);
    }
    public InputTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_task, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateDateLabel(EditText obj) {
        //String myFormat = "dd/MM/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        obj.setText(sdf.format(myCalendar.getTime()));
    }
}
