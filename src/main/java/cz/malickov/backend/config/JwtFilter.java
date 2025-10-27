package cz.malickov.backend.config;

import cz.malickov.backend.error.AuthorizationFailedException;
import cz.malickov.backend.service.JWTService;
import cz.malickov.backend.service.UserDetailsLoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsLoginService userDetailsLoginService;

    public JwtFilter(JWTService jwtService, UserDetailsLoginService userDetailsLoginService){
        this.jwtService = jwtService;
        this.userDetailsLoginService = userDetailsLoginService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {


        String uri = request.getRequestURI();

        // Any token is not expected at login endpoint, skip next block and go to filter
        if(!uri.contains("/login") ) {
            String token = null;
            String email;


            if (request.getCookies() != null) {
                token = Arrays.stream(request.getCookies())
                        .filter(cookie -> "JWT".equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null);
            }

            if (token != null) {
                try {
                    email = jwtService.extractEmail(token);
                } catch (Exception e) {
                    log.warn("Failed to extract email from token: {}", e.getMessage());
                    throw new AuthorizationFailedException();
                }
            } else {
                log.warn("No JWT cookie found in request");
                throw new AuthorizationFailedException();
            }

            // loads user details section
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) { // == null  if I am authenticated (not null), I do not need to continue with authentification
                UserDetails userDetails = userDetailsLoginService.loadUserByUsername(email);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // put user into security context for this session
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
