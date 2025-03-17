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
    private final int STUDENTS_PER_PAGE = 15;

    public AttendancePdfGenerator(Context context, List<Student> studentList, String dateString) {
        this.context = context;
        this.studentList = studentList;
        this.dateString = dateString;
    }

    public void generatePdf() {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setAntiAlias(true);

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18);
        titlePaint.setAntiAlias(true);

        int pageNum = 1;
        int studentIndex = 0;

        while (studentIndex < studentList.size()) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            int nameX = 50;
            int attendanceX = 300;
            int mobileX = 400;

            final int[] y = {50};

            canvas.drawText("SIVESH CHESS ACADEMY", nameX, y[0], titlePaint);
            y[0] += 25;
            canvas.drawText("Attendance Report - " + dateString + " (Page " + pageNum + ")", nameX, y[0], paint);
            y[0] += 30;

            canvas.drawText("Student Name", nameX, y[0], paint);
            canvas.drawText("Attendance", attendanceX, y[0], paint);
            canvas.drawText("Mobile number", mobileX, y[0], paint);
            y[0] += 20;
            canvas.drawLine(nameX, y[0], 550, y[0], paint);
            y[0] += 20;

            final int currentStudentIndex = studentIndex;
            final int currentPageNum = pageNum;

            FirebaseDatabase.getInstance().getReference("attendance").child(dateString).get().addOnSuccessListener(dataSnapshot -> {
                int studentsOnPage = 0;
                int localStudentIndex = currentStudentIndex;
                while (localStudentIndex < studentList.size() && studentsOnPage < STUDENTS_PER_PAGE) {
                    Student student = studentList.get(localStudentIndex);
                    String studentName = student.getName();
                    String studentMobilenumber = student.getMobileNumber1();
                    String studentId = student.getStudentId();
                    Boolean isPresent = dataSnapshot.child(studentId).getValue(Boolean.class);

                    canvas.drawText(studentName, nameX, y[0], paint);
                    canvas.drawText(isPresent != null && isPresent ? "Present" : "Absent", attendanceX, y[0], paint);
                    canvas.drawText(studentMobilenumber != null ? studentMobilenumber : "", mobileX, y[0], paint);
                    y[0] += 20;
                    studentsOnPage++;
                    localStudentIndex++;
                }

                int tempStudentIndex = currentStudentIndex + studentsOnPage;

                canvas.drawLine(attendanceX - 10, 80, attendanceX - 10, y[0], paint);
                canvas.drawLine(mobileX - 10, 80, mobileX - 10, y[0], paint);

                document.finishPage(page);

                if (tempStudentIndex >= studentList.size()) {
                    savePdf(document);
                } else {
                    generateNextPage(document, tempStudentIndex, currentPageNum + 1);
                }
            }).addOnFailureListener(e -> {
                Log.e("PdfGenerator", "Failed to retrieve attendance data: " + e.getMessage());
                Toast.makeText(context, "Failed to retrieve attendance data.", Toast.LENGTH_SHORT).show();
                document.finishPage(page);
                document.close();
            });
            return;
        }
    }

    private void generateNextPage(PdfDocument document, int studentIndex, int pageNum) {
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int nameX = 50;
        int attendanceX = 300;
        int mobileX = 400;

        final int[] y = {50};
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setAntiAlias(true);

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18);
        titlePaint.setAntiAlias(true);

        canvas.drawText("SIVESH CHESS ACADEMY", nameX, y[0], titlePaint);
        y[0] += 25;
        canvas.drawText("Attendance Report - " + dateString + " (Page " + pageNum + ")", nameX, y[0], paint);
        y[0] += 30;

        canvas.drawText("Student Name", nameX, y[0], paint);
        canvas.drawText("Attendance", attendanceX, y[0], paint);
        canvas.drawText("Mobile number", mobileX, y[0], paint);
        y[0] += 20;
        canvas.drawLine(nameX, y[0], 550, y[0], paint);
        y[0] += 20;

        final int currentStudentIndex = studentIndex;

        FirebaseDatabase.getInstance().getReference("attendance").child(dateString).get().addOnSuccessListener(dataSnapshot -> {
            int studentsOnPage = 0;
            int localStudentIndex = currentStudentIndex;
            while (localStudentIndex < studentList.size() && studentsOnPage < STUDENTS_PER_PAGE) {
                Student student = studentList.get(localStudentIndex);
                String studentName = student.getName();
                String studentMobilenumber = student.getMobileNumber1();
                String studentId = student.getStudentId();
                Boolean isPresent = dataSnapshot.child(studentId).getValue(Boolean.class);

                canvas.drawText(studentName, nameX, y[0], paint);
                canvas.drawText(isPresent != null && isPresent ? "Present" : "Absent", attendanceX, y[0], paint);
                canvas.drawText(studentMobilenumber != null ? studentMobilenumber : "", mobileX, y[0], paint);
                y[0] += 20;
                studentsOnPage++;
                localStudentIndex++;
            }

            int tempStudentIndex = currentStudentIndex + studentsOnPage;

            canvas.drawLine(attendanceX - 10, 80, attendanceX - 10, y[0], paint);
            canvas.drawLine(mobileX - 10, 80, mobileX - 10, y[0], paint);

            document.finishPage(page);
            if (tempStudentIndex >= studentList.size()) {
                savePdf(document);
            } else {
                generateNextPage(document, tempStudentIndex, pageNum + 1);
            }
        }).addOnFailureListener(e -> {
            Log.e("PdfGenerator", "Failed to retrieve attendance data: " + e.getMessage());
            Toast.makeText(context, "Failed to retrieve attendance data.", Toast.LENGTH_SHORT).show();
            document.finishPage(page);
            document.close();
        });
    }

    private void savePdf(PdfDocument document) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateandtimeString = formatter.format(calendar.getTime());
        String fileName = "Sivesh_Chess_Attendance_" + dateandtimeString + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(context, "PDF saved to Downloads folder. filename: " + fileName, Toast.LENGTH_SHORT).show();
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