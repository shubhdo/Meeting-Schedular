package vinsol.com.meetingscheduler.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vinsol.com.meetingscheduler.R;
import vinsol.com.meetingscheduler.models.Meeting;

/**
 * Created by HP-User on 5/6/2017.
 */

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MyViewHolder> {
    public Meeting[] meetingsArray;

    public MeetingAdapter(Meeting[] meetingsArray) {
        this.meetingsArray = meetingsArray;
    }

    @Override
    public MeetingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeetingAdapter.MyViewHolder holder, int position) {
        holder.startTime.setText(meetingsArray[position].getStartTime());
        holder.endTime.setText(meetingsArray[position].getEndTime());
        holder.description.setText(meetingsArray[position].getDescription());
        StringBuilder multipleAttendee = new StringBuilder();
        for (int i=0;i<meetingsArray[position].getParticipants().size();i++)
            multipleAttendee.append("  "+meetingsArray[position].getParticipants().get(i));
        holder.attendee.setText(multipleAttendee);
    }

    @Override
    public int getItemCount() {
        return meetingsArray.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView startTime, endTime, description, attendee;

        public MyViewHolder(View itemView) {
            super(itemView);
            startTime = (TextView) itemView.findViewById(R.id.text_start_time);
            endTime = (TextView) itemView.findViewById(R.id.text_end_time);
            description = (TextView) itemView.findViewById(R.id.text_description);
            attendee = (TextView) itemView.findViewById(R.id.text_attendee);
        }
    }
}
