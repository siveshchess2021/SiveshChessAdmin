package com.example.siveshchessadminapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private List<Student> studentList;
    private Context context;
    private DatabaseReference attendanceRef;

    public AttendanceAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
        attendanceRef = FirebaseDatabase.getInstance().getReference("attendance");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.studentNameTextView.setText(student.getName());

        holder.attendanceRadioGroup.setOnCheckedChangeListener(null); // Clear previous listener
        holder.presentRadioButton.setChecked(false);
        holder.absentRadioButton.setChecked(false);

        holder.attendanceRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isPresent = checkedId == R.id.present_radio_button;
            recordAttendance(student.getName(), isPresent);
        });
    }

    private void recordAttendance(String studentId, boolean isPresent) {
        String dateString = getCurrentDateAsString();

        if (studentId != null && dateString != null) {
            attendanceRef.child(dateString).child(studentId).setValue(isPresent)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Attendance recorded", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to record attendance", Toast.LENGTH_SHORT).show());
        } else {
            Log.e("AttendanceAdapter", "studentId or dateString is null. studentId: " + studentId + " dateString: " + dateString);
            Toast.makeText(context, "Failed to record attendance. Null data.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDateAsString() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(currentDate);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameTextView;
        RadioGroup attendanceRadioGroup;
        RadioButton presentRadioButton;
        RadioButton absentRadioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.student_name_text_view);
            attendanceRadioGroup = itemView.findViewById(R.id.attendance_radio_group);
            presentRadioButton = itemView.findViewById(R.id.present_radio_button);
            absentRadioButton = itemView.findViewById(R.id.absent_radio_button);
        }
    }
}