package ru.aston.rest_service.servlet;

import lombok.extern.slf4j.Slf4j;
import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.service.ProductService;
import ru.aston.rest_service.service.impl.ProductServiceImpl;
import ru.aston.rest_service.servlet.dto.product.ProductDtoOut;
import ru.aston.rest_service.servlet.dto.product.ProductNewDtoIn;
import ru.aston.rest_service.servlet.dto.product.ProductUpdateDtoIn;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import static ru.aston.rest_service.exception.dto.ErrorResponse.createError;
import static ru.aston.rest_service.servlet.dto.MessageResponse.createMessage;

@Slf4j
@WebServlet(urlPatterns = "/product/*")
public class ProductServlet extends Servlet {
    private final transient ProductService productService = ProductServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeader(resp);

        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            log.info("Обработка GET /product/{}", pathPart[1]);
            if ("all".equals(pathPart[1])) {
                List<ProductDtoOut> productDtoOutList = productService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(productDtoOutList);
            } else {
                Long productId = Long.parseLong(pathPart[1]);
                ProductDtoOut productDtoOut = productService.findById(productId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(productDtoOut);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = objectMapper.writeValueAsString(createError(resp, e));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = objectMapper.writeValueAsString(createError(resp, e));
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeader(resp);

        String responseAnswer = "";
        Optional<ProductNewDtoIn> productResponse;
        try {
            String json = getJson(req);
            productResponse = Optional.ofNullable(objectMapper.readValue(json, ProductNewDtoIn.class));
            ProductNewDtoIn productNewDtoIn = productResponse.orElseThrow(IllegalArgumentException::new);
            log.info("Обработка POST /product/");
            responseAnswer = objectMapper.writeValueAsString(productService.save(productNewDtoIn));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = objectMapper.writeValueAsString(createError(resp, e));

        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeader(resp);

        String responseAnswer = "";
        Optional<ProductUpdateDtoIn> productResponse;
        try {
            String json = getJson(req);
            productResponse = Optional.ofNullable(objectMapper.readValue(json, ProductUpdateDtoIn.class));
            ProductUpdateDtoIn productUpdateDtoIn = productResponse.orElseThrow(IllegalArgumentException::new);
            log.info("Обработка PUT /product/{}", productUpdateDtoIn.getId());
            productService.update(productUpdateDtoIn);
            responseAnswer = objectMapper.writeValueAsString(createMessage("Изменения в продукт внесены."));
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = objectMapper.writeValueAsString(createError(resp, e));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = objectMapper.writeValueAsString(createError(resp, e));
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeader(resp);
        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long productId = Long.parseLong(pathPart[1]);
            log.info("Обработка DELETE /product/{}", pathPart[1]);
            if (Boolean.TRUE.equals(productService.delete(productId))) {
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(createMessage("Удалено."));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseAnswer = objectMapper.writeValueAsString(createMessage("Не известная ошибка при удалении продукта."));
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = objectMapper.writeValueAsString(createError(resp, e));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = objectMapper.writeValueAsString(createError(resp, e));
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }
}