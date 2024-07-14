package com.jtc.servlet;

import com.jtc.dao.UserDAO;
import com.jtc.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = new User(username, password);
        boolean success = userDAO.registerUser(user);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (success) {
            response.getWriter().write("{\"status\":\"success\"}");
        } else {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Registration failed. Username may already exist.\"}");
        }
    }
}
