package com.example.siveshchessadminapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewStudent extends AppCompatActivity {

    private TextInputEditText studentNameEditText, parentNameEditText, mobileNumber1EditText,
            mobileNumber2EditText, parentOccupationEditText, addressEditText, schoolNameEditText,
            dateOfBirthEditText, emailIdEditText;
    private Button submitButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);
        FirebaseApp.initializeApp(this);

        studentNameEditText = findViewById(R.id.student_name);
        parentNameEditText = findViewById(R.id.parent_name);
        mobileNumber1EditText = findViewById(R.id.mobile_number1);
        mobileNumber2EditText = findViewById(R.id.mobile_number2);
        parentOccupationEditText = findViewById(R.id.parent_occupation);
        addressEditText = findViewById(R.id.address);
        schoolNameEditText = findViewById(R.id.school_name);
        dateOfBirthEditText = findViewById(R.id.date_of_birth);
        emailIdEditText = findViewById(R.id.email_id);
        submitButton = findViewById(R.id.submit_button);

        databaseReference = FirebaseDatabase.getInstance().getReference("student");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
    }

    private void uploadData() {
        String name = studentNameEditText.getText().toString().trim();
        String parentName = parentNameEditText.getText().toString().trim();
        String mobile1 = mobileNumber1EditText.getText().toString().trim();
        String mobile2 = mobileNumber2EditText.getText().toString().trim();
        String occupation = parentOccupationEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String school = schoolNameEditText.getText().toString().trim();
        String dob = dateOfBirthEditText.getText().toString().trim();
        String email = emailIdEditText.getText().toString().trim();

        if (name.isEmpty() || parentName.isEmpty() || mobile1.isEmpty() || occupation.isEmpty() || address.isEmpty() || school.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadToDatabase(name, parentName, mobile1, mobile2, occupation, address, school, dob, email, null);

    }

    private void uploadToDatabase(String name, String parentName, String mobile1, String mobile2,
                                  String occupation, String address, String school, String dob, String email, String imageUrl) {
        String studentId = databaseReference.push().getKey();
        Student student = new Student(name, parentName, mobile1, mobile2, occupation, address, school, dob, email, imageUrl);
        student.setStudentId(studentId); // Crucial: Set studentId here
        databaseReference.child(name).setValue(student)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NewStudent.this, "Student data uploaded successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewStudent.this, "Data upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        studentNameEditText.setText("");
        parentNameEditText.setText("");
        mobileNumber1EditText.setText("");
        mobileNumber2EditText.setText("");
        parentOccupationEditText.setText("");
        addressEditText.setText("");
        schoolNameEditText.setText("");
        dateOfBirthEditText.setText("");
        emailIdEditText.setText("");
    }
}