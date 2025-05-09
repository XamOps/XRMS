package com.XAMMER.HRMS.config; // Or com.XAMMER.HRMS.security, depending on where you created the package

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        System.out.println("Authenticated user authorities: " + authorities); // Add this line

        if (authorities.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (authorities.contains("ROLE_USER")) {
            response.sendRedirect("/dashboard");
        } else {
            response.sendRedirect("/default-dashboard");
        }
    }
}