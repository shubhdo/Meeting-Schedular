package vinsol.com.meetingscheduler;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vinsol.com.meetingscheduler.adapters.MeetingAdapter;
import vinsol.com.meetingscheduler.models.Meeting;
import vinsol.com.meetingscheduler.network.ApiClient;
import vinsol.com.meetingscheduler.network.ApiInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    MeetingAdapter meetingAdapter;
    TextView prev_button, next_button, showDate;
    Button scheduleButton;
    GregorianCalendar gregorianCalendar;
    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prev_button = (TextView) findViewById(R.id.activity_prev_option);
        next_button = (TextView) findViewById(R.id.activity_next_option);
        showDate = (TextView) findViewById(R.id.activity_top_date);
        scheduleButton = (Button) findViewById(R.id.button_schedule_meeting);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        prev_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        scheduleButton.setOnClickListener(this);


        gregorianCalendar = new GregorianCalendar();
        int year = gregorianCalendar.get(Calendar.YEAR);
        int month = gregorianCalendar.get(Calendar.MONTH);
        int day = gregorianCalendar.get(Calendar.DATE);
        final String currentDate = day + "/" + month + "/" + year;


        if (SharedPreferenceHelper.getSharedPreferenceBoolean(this, "first_launch", true)) {
            showDate.setText(currentDate);
            MakeApiCall(currentDate);
            SharedPreferenceHelper.setSharedPreferenceBoolean(this, "first_launch", false);
        } else {
            Gson gson = new Gson();
            String fetchFromSharedPrefrences = SharedPreferenceHelper.getSharedPreferenceString(this, "latest_data", null);
            if (fetchFromSharedPrefrences == null) {

                showDate.setText(currentDate);
                MakeApiCall(currentDate);
            } else {

                //display from saved data
                String date = SharedPreferenceHelper.getSharedPreferenceString(this, "latest_date", currentDate);
                showDate.setText(date);

                Type arrayType = new TypeToken<Meeting[]>() {
                }.getType();
                Meeting[] meetingsArray = gson.fromJson(fetchFromSharedPrefrences, arrayType);
                meetingAdapter = new MeetingAdapter(meetingsArray);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(meetingAdapter);

            }
        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                showDate.setText(currentDate);
                MakeApiCall(currentDate);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void MakeApiCall(final String date) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Meeting[]> call = apiInterface.getMeetingSchedularResponse(date);
        call.enqueue(new Callback<Meeting[]>() {
            @Override
            public void onResponse(Call<Meeting[]> call, Response<Meeting[]> response) {
                if (response.isSuccessful()) {
                    Meeting[] meetingsArray = response.body();
                    meetingAdapter = new MeetingAdapter(meetingsArray);
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();


                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(meetingAdapter);

                    Gson gson = new Gson();
                    Type arrayType = new TypeToken<Meeting[]>() {
                    }.getType();
                    String json = gson.toJson(meetingsArray, arrayType);
                    SharedPreferenceHelper.setSharedPreferenceString(MainActivity.this, "latest_data", json);
                    SharedPreferenceHelper.setSharedPreferenceString(MainActivity.this, "latest_date", date);


                } else {
                    Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Meeting[]> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int year, month, day;
        String currentDate;
        switch (id) {
            case R.id.activity_next_option:
                gregorianCalendar.add(Calendar.DATE, 1);
                if (gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    gregorianCalendar.add(Calendar.DATE, 1);

                } else {
                    year = gregorianCalendar.get(Calendar.YEAR);
                    month = gregorianCalendar.get(Calendar.MONTH);
                    day = gregorianCalendar.get(Calendar.DATE);
                    currentDate = day + "/" + month + "/" + year;

                    showDate.setText(currentDate);

                    MakeApiCall(currentDate);
                }
                break;
            case R.id.activity_prev_option:
                gregorianCalendar.add(Calendar.DATE, -1);
                if (gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    gregorianCalendar.add(Calendar.DATE, -1);

                } else {
                    year = gregorianCalendar.get(Calendar.YEAR);
                    month = gregorianCalendar.get(Calendar.MONTH);
                    day = gregorianCalendar.get(Calendar.DATE);
                    currentDate = day + "/" + month + "/" + year;
                    showDate.setText(currentDate);

                    MakeApiCall(currentDate);
                }
                break;
            case R.id.button_schedule_meeting:
                Intent intent = new Intent(this, ScheduleMeetingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

                break;
            default:
        }

    }

}
