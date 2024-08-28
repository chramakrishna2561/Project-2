package com.adobe.aem.guides.project2.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Random;

@Component(service = { Servlet.class })
@SlingServletPaths("/bin/randomGenerator")

public class RandomGenerator extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String type = request.getParameter("type");

        response.setContentType("text/plain");

        String result;
        switch (type != null ? type.toLowerCase() : "") {
            case "number":
                result = generateRandomNumbers(6);
                break;
            case "letters":
                result = generateRandomLetters(6);
                break;
            case "random":
                result = generateRandomMixed(6);
                break;
            default:
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                result = "Invalid parameter. Use 'number', 'letters', or 'random'.";
                break;
        }

        response.getWriter().write(result);
    }

    private String generateRandomNumbers(int count) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateRandomLetters(int count) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            char letter = (char) (random.nextInt(26) + 'A');
            sb.append(letter);
        }
        return sb.toString();
    }

    private String generateRandomMixed(int count) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            if (i < count / 2) {
                sb.append(random.nextInt(10));
            } else {
                char letter = (char) (random.nextInt(26) + 'A');
                sb.append(letter);
            }
        }
        return sb.toString();
    }
}
