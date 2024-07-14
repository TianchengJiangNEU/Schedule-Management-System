package com.jtc.servlet;

import com.jtc.dao.UserDAO;
import com.jtc.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDAO.login(username, password);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.getWriter().write("{\"status\":\"success\"}");
        } else {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid username or password.\"}");
        }
    }
}
