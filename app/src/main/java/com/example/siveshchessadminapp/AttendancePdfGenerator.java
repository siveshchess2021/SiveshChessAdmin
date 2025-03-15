package com.example.siveshchessadminapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendancePdfGenerator {

    private Context context;
    private List<Student> studentList;
    private String dateString;

    public AttendancePdfGenerator(Context context, List<Student> studentList, String dateString) {
        this.context = context;
        this.studentList = studentList;
        this.dateString = dateString;
    }

    public void generatePdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setAntiAlias(true);

        final int[] y = {50}; // Starting Y position

        // Fixed X-coordinates for columns
        int nameX = 50;
        int attendanceX = 300;
        int mobileX = 400;

        // Increase font size for "SIVESH CHESS ACADEMY"
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18); // Increased font size
        titlePaint.setAntiAlias(true);

        canvas.drawText("SIVESH CHESS ACADEMY", nameX, y[0], titlePaint);
        y[0] += 25; // Adjusted spacing after title
        canvas.drawText("Attendance Report - " + dateString, nameX, y[0], paint);
        y[0] += 30;

        // Draw header row
        canvas.drawText("Student Name", nameX, y[0], paint);
        canvas.drawText("Attendance", attendanceX, y[0], paint);
        canvas.drawText("Mobile number", mobileX, y[0], paint);
        y[0] += 20;
        canvas.drawLine(nameX, y[0], 550, y[0], paint); // Header row line
        y[0] += 20;

        FirebaseDatabase.getInstance().getReference("attendance").child(dateString).get().addOnSuccessListener(dataSnapshot -> {
            for (Student student : studentList) {
                String studentName = student.getName();
                String studentMobilenumber = student.getMobileNumber1();
                Boolean isPresent = dataSnapshot.child(studentName).getValue(Boolean.class);

                // Draw row data
                canvas.drawText(studentName, nameX, y[0], paint);
                canvas.drawText(isPresent != null && isPresent ? "Present" : "Absent", attendanceX, y[0], paint);
                canvas.drawText(studentMobilenumber != null ? studentMobilenumber : "", mobileX, y[0], paint);
                y[0] += 20;
            }

            // Draw column lines
            canvas.drawLine(attendanceX - 10, 80, attendanceX - 10, y[0], paint); // Attendance column line
            canvas.drawLine(mobileX - 10, 80, mobileX - 10, y[0], paint); // Mobile column line

            document.finishPage(page);
            savePdf(document);
        }).addOnFailureListener(e -> {
            Log.e("PdfGenerator", "Failed to retrieve attendance data: " + e.getMessage());
            Toast.makeText(context, "Failed to retrieve attendance data.", Toast.LENGTH_SHORT).show();
            document.finishPage(page);
            document.close();
        });
    }

    private void savePdf(PdfDocument document) {
        String fileName = "Attendance_" + dateString + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(context, "PDF saved to Downloads folder.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("PdfGenerator", "Error saving PDF: " + e.getMessage());
            Toast.makeText(context, "Error saving PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getCurrentDateAsString() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(currentDate);
    }
}