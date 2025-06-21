package com.board;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReadTrackingManager {
    public Set<Integer> getReadQuestions(HttpServletRequest request, HttpSession session, Principal principal) {
        Set<Integer> readQuestions = new LinkedHashSet<>();

        if (principal != null) {
            Object attr = session.getAttribute("readQuestions");
            if (attr instanceof Set) {
                readQuestions = new LinkedHashSet<>((Set<Integer>) attr);
            }
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("readQuestions")) {
                        try {
                            String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                            for (String idStr : decoded.split(",")) {
                                try {
                                    readQuestions.add(Integer.parseInt(idStr.trim()));
                                } catch (NumberFormatException ignored) {}
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        }

        return readQuestions;
    }

    public void saveReadQuestion(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                 Principal principal, Integer id) {
        Set<Integer> readQuestions = getReadQuestions(request, session, principal);
        if (readQuestions.contains(id)) return;

        readQuestions.add(id);

        if (principal != null) {
            session.setAttribute("readQuestions", readQuestions);
        } else {
            try {
                String joined = readQuestions.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                String encoded = URLEncoder.encode(joined, StandardCharsets.UTF_8);
                Cookie cookie = new Cookie("readQuestions", encoded);
                cookie.setPath("/");
                cookie.setMaxAge(30 * 24 * 60 * 60); // 30일
                response.addCookie(cookie);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
