package com.rv.jwt;

import com.rv.userdetails.PlatformUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final PlatformUserDetailsService platformUserDetailService;
    private final JwtService jwtService;
    private static final Logger log = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    public JwtAuthenticationFilter(PlatformUserDetailsService platformUserDetailService, JwtService jwtService) {
        this.platformUserDetailService = platformUserDetailService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7).trim();
        try {
            String username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = platformUserDetailService.loadUserByUsername(username);
                if (userDetails != null && jwtService.isTokenValid(jwt)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException |
                 IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Authentication failed: " + ex.getMessage());
            log.log(Level.SEVERE, "JWT Authentication failed", ex);
        } catch (AuthenticationException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed: " + ex.getMessage());
            log.log(Level.SEVERE, "Authentication failed", ex);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred during authentication");
            log.log(Level.SEVERE, "Authentication error", ex);
        }
    }
}
