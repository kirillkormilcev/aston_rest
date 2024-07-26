package ru.aston.rest_service.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.rest_service.exception.JsonException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class Servlet extends HttpServlet {

    final ObjectMapper objectMapper;

    public Servlet() {
        this.objectMapper = new ObjectMapper();
    }

    static void setJsonHeader(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    static String getJson(HttpServletRequest req) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader postData = req.getReader();
            String line;
            while ((line = postData.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new JsonException("Ошибка при конвертации JSON.");
        }
    }
}