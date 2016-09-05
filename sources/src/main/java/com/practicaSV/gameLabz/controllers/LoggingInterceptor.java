package com.practicaSV.gameLabz.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;

public class LoggingInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String line;
        StringBuilder reqLog = new StringBuilder();
        BufferedReader reader;

        try {
            reader = request.getReader();
        } catch (IllegalStateException e) {

            InputStream inputStream = request.getInputStream();
            String charsetName = request.getCharacterEncoding();
            if (charsetName == null) {
                charsetName = "UTF-8";
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charsetName);
            reader = new BufferedReader(inputStreamReader);
        }

        reqLog.append("REST request - ");
        reqLog.append("[Request method: " + request.getMethod() + "] ");
        reqLog.append("[Path info: " + request.getPathInfo() + "] ");

        if (reader != null) {

            reqLog.append("[Request body: ");

            while ((line = reader.readLine()) != null) {

                reqLog.append(line).append(" ");
            }

            reqLog.append("] ");
            reader.close();
        }

        reqLog.append("[Remote address: " + request.getRemoteAddr() + "] ");

        logger.info(reqLog.toString());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        StringBuilder resLog = new StringBuilder();

        resLog.append("REST response - ");
        resLog.append("[Http status: " + response.getStatus() + "] ");
        resLog.append("[Headers: ");

        Collection<String> headerNames = response.getHeaderNames();

        for (String headers: headerNames) {

            resLog.append(response.getHeader(headers) + "; ");
        }
        resLog.append("] ");


        logger.info(resLog.toString());
    }
}
