package com.said.dsbank.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.said.dsbank.res.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
@RequiredArgsConstructor
public  class CustumAccessDenialHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws java.io.IOException, ServletException {
        Response<?> errorResponse = Response.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(accessDeniedException.getMessage())
                .build();

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }


  }




