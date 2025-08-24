package com.example.competr.portal.filters;

import com.example.competr.portal.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println(path);


        if (path.equals("/api/players/register") || path.equals("/api/players/login")) {
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        System.out.println("[JwtAuthFilter] Request URI: " + request.getRequestURI());
        System.out.println("[JwtAuthFilter] Authorization header: " + (authHeader != null ? "Present" : "NULL"));

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("[JwtAuthFilter] JWT token extracted: " + (jwt != null ? "Yes" : "No"));
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("[JwtAuthFilter] Username extracted: " + username);
            } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
                System.out.println("[JwtAuthFilter] JWT validation failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        } else {
            System.out.println("[JwtAuthFilter] No valid Authorization header found");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("[JwtAuthFilter] Attempting to load user details for: " + username);
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("[JwtAuthFilter] User details loaded successfully");

                if (!jwtUtil.isTokenExpired(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JwtAuthFilter] Authentication set successfully");
                } else {
                    System.out.println("[JwtAuthFilter] Token is expired");
                }
            } catch (Exception e) {
                System.out.println("[JwtAuthFilter] Error loading user details: " + e.getMessage());
                // For now, let the request continue without authentication
                // This allows the API to work even if user details can't be loaded
            }
        }

        chain.doFilter(request, response);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
