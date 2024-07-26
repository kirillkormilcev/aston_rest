package ru.aston.rest_service.servlet;

import lombok.extern.slf4j.Slf4j;
import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.service.OrderDetailService;
import ru.aston.rest_service.service.impl.OrderDetailServiceImpl;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailDtoOut;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailNewDtoIn;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailUpdateDtoIn;

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
@WebServlet(urlPatterns = "/order/*")
public class OrderDetailServlet extends Servlet {
    private final transient OrderDetailService orderDetailService = OrderDetailServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeader(resp);

        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            log.info("Обработка GET /order/{}", pathPart[1]);
            if ("all".equals(pathPart[1])) {
                List<OrderDetailDtoOut> orderDetailDtoOutList = orderDetailService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(orderDetailDtoOutList);
            } else if ("PAID".equals(pathPart[1])) {
                List<OrderDetailDtoOut> orderDetailDtoOutList = orderDetailService.findAllWithCondition("PAID");
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(orderDetailDtoOutList);
            } else {
                Long orderDetailId = Long.parseLong(pathPart[1]);
                OrderDetailDtoOut orderDetailDtoOut = orderDetailService.findById(orderDetailId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(orderDetailDtoOut);
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
        try {
            String json = getJson(req);
            Optional<OrderDetailNewDtoIn> orderDetailResponse;
            log.info("Обработка POST /order/");
            orderDetailResponse = Optional.ofNullable(objectMapper.readValue(json, OrderDetailNewDtoIn.class));
            OrderDetailNewDtoIn orderDetailNewDtoIn = orderDetailResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(orderDetailService.save(orderDetailNewDtoIn));
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
        Optional<OrderDetailUpdateDtoIn> orderDetailResponse;
        try {
            String json = getJson(req);
            orderDetailResponse = Optional.ofNullable(objectMapper.readValue(json, OrderDetailUpdateDtoIn.class));
            OrderDetailUpdateDtoIn orderDetailUpdateDtoIn = orderDetailResponse.orElseThrow(IllegalArgumentException::new);
            orderDetailService.update(orderDetailUpdateDtoIn);
            log.info("Обработка PUT /order/{}", orderDetailUpdateDtoIn.getId());
            responseAnswer = objectMapper.writeValueAsString(createMessage("Изменения в заказ внесены."));
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
            if (Boolean.TRUE.equals(orderDetailService.delete(productId))) {
                log.info("Обработка DELETE /order/{}", productId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(createMessage("Удалено."));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseAnswer = objectMapper.writeValueAsString(createMessage("Не известная ошибка при удалении заказа."));
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