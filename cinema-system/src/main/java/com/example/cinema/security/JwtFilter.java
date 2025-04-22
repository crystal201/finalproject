    package com.example.cinema.security;

    import java.io.IOException;

    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import com.example.cinema.service.UserService;

    import io.jsonwebtoken.JwtException;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;

    @Component
    public class JwtFilter extends OncePerRequestFilter {
        private final JwtUtil jwtUtil;
        private final UserService userService;
    
        public JwtFilter(JwtUtil jwtUtil, UserService userService) {
            this.jwtUtil = jwtUtil;
            this.userService = userService;
        }
    
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            String authHeader = request.getHeader("Authorization");
            String path = request.getRequestURI();
            System.out.println("PATH: " + path + " | AUTH HEADER: " + authHeader);
    
            if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
                System.out.println("Allowing login/register without JWT");
                chain.doFilter(request, response);
                return;
            }
    
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("No valid Bearer token found");
                chain.doFilter(request, response);
                return;
            }
    
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                System.out.println("Extracted username: " + username);
                UserDetails userDetails = userService.loadUserByUsername(username);
                if (userDetails == null) {
                    System.err.println("User not found: " + username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                    return;
                }
                System.out.println("UserDetails: " + userDetails.getUsername());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                System.err.println("JWT Error: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getMessage());
                return;
            } catch (UsernameNotFoundException e) {
                System.err.println("Username not found: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found: " + e.getMessage());
                return;
            }
    
            chain.doFilter(request, response);
        }
    }