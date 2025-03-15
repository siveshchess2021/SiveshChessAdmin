package com.example.siveshchessadminapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AgeCalculator {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calculateAge(String dobString) {
        if (dobString == null || dobString.isEmpty()) {
            return -1; // Or throw an exception, or handle it as appropriate
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dob = LocalDate.parse(dobString, formatter);
            LocalDate currentDate = LocalDate.now();

            if ((dob != null) && (currentDate != null)) {
                return Period.between(dob, currentDate).getYears();
            } else {
                return -1; // Or throw an exception
            }

        } catch (DateTimeParseException e) {
            // Handle invalid date format
            System.err.println("Invalid date format: " + dobString);
            return -1; // Or throw an exception
        } catch (Exception e) {
            // Handle other exceptions
            System.err.println("Error calculating age: " + e.getMessage());
            return -1; // or throw an exception.
        }
    }

}