package com.jtc.servlet;

import com.jtc.dao.ScheduleDAO;
import com.jtc.model.Schedule;
import com.jtc.model.User;
import com.google.gson.Gson;

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
import java.util.List;

@WebServlet("/querySchedules")
public class QueryScheduleServlet extends HttpServlet {
    private ScheduleDAO scheduleDAO = new ScheduleDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // This will make the date parsing strict

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            // Validate date format
            if (!isValidDateFormat(startDateStr) || !isValidDateFormat(endDateStr)) {
                throw new ParseException("Invalid date format", 0);
            }

            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            // Add one day to endDate to include the entire last day
            endDate = new Date(endDate.getTime() + 24 * 60 * 60 * 1000);

            List<Schedule> schedules = scheduleDAO.querySchedules(user.getId(), new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()));

            response.getWriter().write(new Gson().toJson(schedules));
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error parsing date: " + e.getMessage());
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid date format. Please use YYYY-MM-DD format.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error querying schedules: " + e.getMessage());
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private boolean isValidDateFormat(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}