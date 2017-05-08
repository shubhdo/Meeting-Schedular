package vinsol.com.meetingscheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vinsol.com.meetingscheduler.adapters.MeetingAdapter;
import vinsol.com.meetingscheduler.models.Meeting;
import vinsol.com.meetingscheduler.network.ApiClient;
import vinsol.com.meetingscheduler.network.ApiInterface;

public class ScheduleMeetingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText dateSelector, startTimeSelector, endTimeSelector, description;
    Button submit;
    TextView back_button;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener datePickerDialog;
    Calendar setStartTime;
    Calendar setEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_meeting);

        dateSelector = (EditText) findViewById(R.id.date_selector);
        startTimeSelector = (EditText) findViewById(R.id.start_time_selector);
        endTimeSelector = (EditText) findViewById(R.id.end_time_selector);
        back_button = (TextView) findViewById(R.id.activity_prev_option);
        submit = (Button) findViewById(R.id.button_submit);

        dateSelector.setOnClickListener(this);
        startTimeSelector.setOnClickListener(this);
        endTimeSelector.setOnClickListener(this);
        back_button.setOnClickListener(this);
        submit.setOnClickListener(this);

    }


    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateSelector.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.date_selector: {
                calendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };
                new DatePickerDialog(this, datePickerDialog, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
            break;

            case R.id.start_time_selector: {
                setStartTime = Calendar.getInstance();
                int hour = setStartTime.get(Calendar.HOUR_OF_DAY);
                int minute = setStartTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTimeSelector.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
            break;

            case R.id.end_time_selector: {
                setEndTime = Calendar.getInstance();
                int hour = setEndTime.get(Calendar.HOUR_OF_DAY);
                int minute = setEndTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTimeSelector.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
            break;
            case R.id.button_submit: {
                MakeApiCallAndCheckSchedule(dateSelector.getText().toString());

            }
            break;

            case R.id.activity_prev_option:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    public void MakeApiCallAndCheckSchedule(final String date) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Meeting[]> call = apiInterface.getMeetingSchedularResponse(date);
        call.enqueue(new Callback<Meeting[]>() {
            @Override
            public void onResponse(Call<Meeting[]> call, Response<Meeting[]> response) {
                if (response.isSuccessful()) {
                    Meeting[] meetingArray = response.body();
                    for (int i = 0; i < meetingArray.length; i++) {
                        Meeting aMeetingArray = meetingArray[i];

                        int scheduledStartTime = Integer.parseInt(startTimeSelector.getText().toString().split(":")[0]);
                        int scheduledEndTime = Integer.parseInt(startTimeSelector.getText().toString().split(":")[0]);
                        int fetchedStartTime = Integer.parseInt(aMeetingArray.getStartTime().split(":")[0]);
                        int fetchedEndTime = Integer.parseInt(aMeetingArray.getEndTime().split(":")[0]);


                        if (scheduledStartTime < fetchedStartTime && scheduledEndTime < fetchedEndTime) {
                            Toast.makeText(ScheduleMeetingActivity.this, "Meeting Booked", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (scheduledStartTime > fetchedStartTime && scheduledEndTime > fetchedEndTime) {
                            Toast.makeText(ScheduleMeetingActivity.this, "Meeting Booked", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (i == meetingArray.length - 1) {
                            Toast.makeText(ScheduleMeetingActivity.this, "Checked for next day", Toast.LENGTH_SHORT).show();
                        }

                    }
/*
                    Toast.makeText(ScheduleMeetingActivity.this, "Success", Toast.LENGTH_SHORT).show();
*/
                } else {
/*
                    Toast.makeText(ScheduleMeetingActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
*/
                }
            }

            @Override
            public void onFailure(Call<Meeting[]> call, Throwable t) {
                Toast.makeText(ScheduleMeetingActivity.this, "Fail", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}

