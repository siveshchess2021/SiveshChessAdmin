package com.example.siveshchessadminapp;

import static com.example.siveshchessadminapp.AgeCalculator.calculateAge;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siveshchessadminapp.R;
import com.example.siveshchessadminapp.Student;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_card_item, parent, false);
        return new StudentViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.studentNameTextView.setText(student.getName());
        holder.parentNameTextView.setText("Parent         : " + student.getParentName());
        holder.mobileNumberTextView.setText("Mobile         : " + student.getMobileNumber1());
        holder.parentOccupation.setText("Occupation : " + student.getParentOccupation());
        holder.address.setText("Address       : " + student.getAddress());
        holder.schoolName.setText("School name: " + student.getSchoolName());
//        holder.ageTextView.setText("Age        : " + student.getMobileNumber1());
        holder.dob.setText("DOB              : " + student.getDateOfBirth());

        int age = calculateAge(student.getDateOfBirth());
        if (age >= 0) {
            holder.ageTextView.setText("Age               : " + age);
        } else {
            holder.ageTextView.setText("Invalid DOB");
        }
        // Set other TextViews with student data
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameTextView;
        TextView parentNameTextView;
        TextView mobileNumberTextView;
        TextView ageTextView;
        TextView parentOccupation;
        TextView address;
        TextView schoolName;
        TextView dob;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.student_name_text_view);
            parentNameTextView = itemView.findViewById(R.id.parent_name_text_view);
            mobileNumberTextView = itemView.findViewById(R.id.mobile_number_text_view);
            ageTextView = itemView.findViewById(R.id.age_text_view);
            parentOccupation = itemView.findViewById(R.id.parent_occupation_text_view);
            address = itemView.findViewById(R.id.address_text_view);
            schoolName = itemView.findViewById(R.id.schoolname_text_view);
            dob = itemView.findViewById(R.id.dob_text_view);
            // Initialize other TextViews
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        private int calculateAge(String dobString) {
            if (dobString == null || dobString.isEmpty()) {
                return -1; // Indicate an error
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dob = LocalDate.parse(dobString, formatter);
                LocalDate currentDate = LocalDate.now();

                return Period.between(dob, currentDate).getYears();
            } catch (DateTimeParseException e) {
                return -1; // Indicate an error
            }
        }
    }
}