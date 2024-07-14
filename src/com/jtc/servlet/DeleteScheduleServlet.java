package com.jtc.servlet;

import com.jtc.dao.ScheduleDAO;
import com.jtc.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/deleteSchedule")
public class DeleteScheduleServlet extends HttpServlet {
    private ScheduleDAO scheduleDAO = new ScheduleDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int scheduleId = Integer.parseInt(request.getParameter("scheduleId"));
        boolean success = scheduleDAO.deleteSchedule(scheduleId, user.getId());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (success) {
            response.getWriter().write("{\"status\":\"success\"}");
        } else {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to delete schedule.\"}");
        }
    }
}
