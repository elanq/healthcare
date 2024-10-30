package com.fastcampus.healthcare.middleware;

import com.fastcampus.healthcare.service.JwtService;
import com.fastcampus.healthcare.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final UserDetailsImpl userDetailService;
  private final JwtService jwtService;
  private final HandlerExceptionResolver handlerExceptionResolver;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      try {
        filterChain.doFilter(request, response);
      } catch (InsufficientAuthenticationException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
      }
      return;
    }

    try {
      final String jwt = authHeader.substring(7);
      final String username = jwtService.getUsernameFromToken(jwt);

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (username != null && authentication == null) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource()
            .buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }

      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      handlerExceptionResolver.resolveException(request, response, null, ex);
    }

  }
}
