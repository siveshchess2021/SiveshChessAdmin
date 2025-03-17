package com.example.siveshchessadminapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private List<Student> studentList;
    private Context context;
    private DatabaseReference attendanceRef;
    private Map<String, Boolean> attendanceStates = new HashMap<>();
    private boolean isResetting = false;
    private OnAttendanceChangeListener attendanceChangeListener;

    public interface OnAttendanceChangeListener {
        void onPresentCountChanged(int presentCount);
    }

    public AttendanceAdapter(List<Student> studentList, Context context, OnAttendanceChangeListener listener) {
        this.studentList = studentList;
        this.context = context;
        this.attendanceChangeListener = listener;
        attendanceRef = FirebaseDatabase.getInstance().getReference("attendance");
        loadInitialAttendanceStates();
    }

    private void loadInitialAttendanceStates() {
        if (isResetting) {
            return;
        }
        String dateString = getCurrentDateAsString();
        attendanceRef.child(dateString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attendanceStates.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    attendanceStates.put(snapshot.getKey(), snapshot.getValue(Boolean.class));
                }
                Log.d("AttendanceAdapter", "Initial states loaded: " + attendanceStates.toString());
                notifyDataSetChanged();
                updatePresentCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AttendanceAdapter", "Failed to load initial attendance states: " + databaseError.getMessage());
            }
        });
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

        holder.attendanceRadioGroup.setOnCheckedChangeListener(null);

        boolean isPresent = attendanceStates.containsKey(student.getStudentId()) && attendanceStates.get(student.getStudentId());
        holder.presentRadioButton.setChecked(isPresent);
        holder.absentRadioButton.setChecked(!isPresent);

        // Set initial tint colors
        setRadioButtonColor(holder.presentRadioButton, isPresent ? Color.GREEN : Color.BLACK); // Use gray for unchecked
        setRadioButtonColor(holder.absentRadioButton, !isPresent ? Color.RED : Color.BLACK);

        holder.attendanceRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean newIsPresent = checkedId == R.id.present_radio_button;
            recordAttendance(student.getStudentId(), newIsPresent);
            attendanceStates.put(student.getStudentId(), newIsPresent);
            updatePresentCount();

            // Update tint colors based on selection
            setRadioButtonColor(holder.presentRadioButton, newIsPresent ? Color.GREEN : Color.BLACK);
            setRadioButtonColor(holder.absentRadioButton, !newIsPresent ? Color.RED : Color.BLACK);
        });
    }

    private void setRadioButtonColor(RadioButton radioButton, int color) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked}, // Checked state
                        new int[]{-android.R.attr.state_checked}  // Unchecked state
                },
                new int[]{color,Color.GRAY } // Checked color, Unchecked color (you can adjust)
        );
        CompoundButtonCompat.setButtonTintList(radioButton, colorStateList);
    }

    private void recordAttendance(String studentId, boolean isPresent) {
        String dateString = getCurrentDateAsString();

        if (studentId != null && dateString != null) {
            attendanceRef.child(dateString).child(studentId).setValue(isPresent)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("AttendanceAdapter", "Attendance recorded: studentId=" + studentId + ", isPresent=" + isPresent);
                    })
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

    public void clearAttendanceStates() {
        isResetting = true;
        attendanceStates.clear();
        notifyDataSetChanged();
        isResetting = false;
        updatePresentCount();
    }

    private void updatePresentCount() {
        int presentCount = 0;
        for (boolean isPresent : attendanceStates.values()) {
            if (isPresent) {
                presentCount++;
            }
        }
        if (attendanceChangeListener != null) {
            attendanceChangeListener.onPresentCountChanged(presentCount);
        }
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