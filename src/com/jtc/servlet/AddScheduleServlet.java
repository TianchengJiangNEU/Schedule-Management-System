package com.jtc.servlet;

import com.jtc.dao.ScheduleDAO;
import com.jtc.model.Schedule;
import com.jtc.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/addSchedule")
public class AddScheduleServlet extends HttpServlet {
    private ScheduleDAO scheduleDAO = new ScheduleDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false); // This will make the date parsing strict

        try {
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");

            // Validate date format
            if (!isValidDateTimeFormat(startTimeStr) || !isValidDateTimeFormat(endTimeStr)) {
                throw new ParseException("Invalid date format", 0);
            }

            Date startTime = sdf.parse(startTimeStr);
            Date endTime = sdf.parse(endTimeStr);
            String title = request.getParameter("title");
            String description = request.getParameter("description");

            Schedule schedule = new Schedule(user.getId(), startTime, endTime, title, description);

            System.out.println("Attempting to add schedule: " + schedule);

            if (scheduleDAO.hasConflict(schedule)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Schedule conflicts with existing schedules.\"}");
                return;
            }

            boolean success = scheduleDAO.addSchedule(schedule);

            System.out.println("Add schedule result: " + success);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (success) {
                response.getWriter().write("{\"status\":\"success\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to add schedule.\"}");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error parsing date: " + e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid date format. Please use YYYY-MM-DD HH:mm format.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error adding schedule: " + e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private boolean isValidDateTimeFormat(String dateTime) {
        return dateTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
    }
}