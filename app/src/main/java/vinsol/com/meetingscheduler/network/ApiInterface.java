package vinsol.com.meetingscheduler.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vinsol.com.meetingscheduler.models.Meeting;

/**
 * Created by HP-User on 5/6/2017.
 */

public interface ApiInterface {
    @GET("api/schedule")
    Call<Meeting[]> getMeetingSchedularResponse(@Query("date") String date);
}
