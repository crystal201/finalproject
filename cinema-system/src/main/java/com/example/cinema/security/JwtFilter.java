    package com.example.cinema.security;

    import java.io.IOException;

    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
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
        
            // Kiểm tra lại logic bỏ qua route đăng nhập
            if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
                System.out.println("Allowing login/register without JWT");
                chain.doFilter(request, response); // Không cần kiểm tra JWT
                return;
            }
        
            // Nếu có Authorization header, kiểm tra token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }
        
            String token = authHeader.substring(7); // Lấy token sau "Bearer "
            try {
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = userService.loadUserByUsername(username);
        
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        
            chain.doFilter(request, response);
        }
        
    }