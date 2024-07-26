package ru.aston.rest_service.servlet;

import lombok.extern.slf4j.Slf4j;
import ru.aston.rest_service.db.ConnectionManager;
import ru.aston.rest_service.db.ConnectionManagerImpl;
import ru.aston.rest_service.utility.SqlSchemeAndDataInit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * На странице выполняется
 * пересоздание таблиц базы данных
 * и их заполнение тестовыми данными
 */
@Slf4j
@WebServlet(urlPatterns = "/reset")
public class ResetPageServlet extends Servlet {
    private final ConnectionManager connection = ConnectionManagerImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        try (PrintWriter out = resp.getWriter()) {
            SqlSchemeAndDataInit.initSqlScheme(connection);
            SqlSchemeAndDataInit.initSqlData(connection);
            out.println("<html><body>");
            out.println("<h1>Tables and initial data recreated!</h1>");
            out.println("<br>");
            out.println("<h2><a href=\"http://31.128.40.167:8085/aston_rest-1.0-SNAPSHOT/\">To home page</a></h2>");
            out.println("</body></html>");
            log.info("Обработка GET /reset.");
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}