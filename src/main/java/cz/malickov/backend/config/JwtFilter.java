package cz.malickov.backend.config;

import cz.malickov.backend.service.JWTService;
import cz.malickov.backend.service.UserDetailsLoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

        // Skip JWT filter for Swagger and login endpoints
        if (uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars")
                || uri.equals("/api/auth/login")
                || uri.equals("/api/auth/authentication")
                || uri.equals("/api/auth/logout")
                || uri.equals("/api/auth/setPassword")
                || uri.equals("/api/user/hello")
        ) {

            filterChain.doFilter(request, response);
            return;
        }

        // Token is not expected at login endpoint, skip next block and go to filter
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
                log.info("Failed to extract email from token: {}", e.getMessage());
                writeUnauthorized(response);
                return;
           }
        } else {
            log.info("No JWT cookie found in the request");
            writeUnauthorized(response);
            return;
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

        filterChain.doFilter(request, response);
    }

    /*
     * My custom Exceptions raised here are thrown from inside JwtFilter, which extends OncePerRequestFilter.
     * Exceptions thrown from a servlet filter bypass DispatcherServlet and bubble up to the servlet container, which typically returns a generic 500
     * So I cannot use here custom exceptions. Thus this method just not to get 500 on FE.
     * @param HttpServletResponse: response object
     * @param String: message
     * @return void
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Authorization failed.");
    }
}
