package ru.aston.rest_service.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@WebServlet(urlPatterns = "/")
public class HomePageServlet extends Servlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<html><body>");
            out.println("<h1>Restaurant Service!</h1>");
            out.println("<br>");
            out.println("<h2><a href=\"http://31.128.40.167:8085/aston_rest-1.0-SNAPSHOT/reset\">Recreate tables and initial data.</a></h2>");
            out.println("</body></html>");
            log.info("Обработка GET / (главная страница).");
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}