package com.example.siveshchessadminapp;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity implements AttendanceAdapter.OnAttendanceChangeListener {

    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private List<Student> studentList;
    private DatabaseReference databaseReference;
    private TextView presentCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        getWindow().setStatusBarColor(ContextCompat.getColor(AttendanceActivity.this, R.color.app_purp));

        presentCountTextView = findViewById(R.id.present_count_text_view); // Make sure you have this TextView in your activity_attendance.xml
        recyclerView = findViewById(R.id.attendance_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentList = new ArrayList<>();
        adapter = new AttendanceAdapter(studentList, this, this); // Pass 'this' as the OnAttendanceChangeListener
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("student");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Student student = snapshot.getValue(Student.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        Button generatePdfButton = findViewById(R.id.generate_pdf_button);
        generatePdfButton.setOnClickListener(v -> {
            Context context = this;
            if (context != null) {
                String date = AttendancePdfGenerator.getCurrentDateAsString();
                AttendancePdfGenerator pdfGenerator = new AttendancePdfGenerator(context, studentList, date);
                pdfGenerator.generatePdf();
                adapter.clearAttendanceStates();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPresentCountChanged(int presentCount) {
        presentCountTextView.setText("Present Students: " + presentCount);
    }
}