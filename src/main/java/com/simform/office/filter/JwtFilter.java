package com.simform.office.filter;

import com.simform.office.service.UserDetailsServiceImpl;
import com.simform.office.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        String header = httpServletRequest.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer")) {
            token = header.substring(7);
        }

        if (token != null) {
            try {
                username = JwtUtil.extractUsername(token);
            } catch (IllegalArgumentException e) {
                log.error("An error occurred while getting username from token: ", e);
                response.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
            } catch (Exception e) {
                log.error("An error occurred while processing authentication : ", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null && JwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("Authenticated user " + username + ", setting security context!");
                }
            }
        }
        filterChain.doFilter(httpServletRequest, response);
    }
}


