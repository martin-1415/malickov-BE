package cz.malickov.backend.config;

import cz.malickov.backend.error.ApiException;
import cz.malickov.backend.service.JWTService;
import cz.malickov.backend.service.UserDetailsLoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String uri = request.getRequestURI();

        // I do not expect any token in login endpoint, skip next block, go to folter
        if(!uri.contains("/login")) {
            String token;
            String email;

            try {
                token = Arrays.stream(request.getCookies())
                        .filter(cookie -> "JWT".equals(cookie.getName()))
                        .findFirst()
                        .orElse(null)
                        .getValue();

                email = jwtService.extractEmail(token);
            }catch (Exception e){ // no cookie, expired token,...
                log.warn("Authorization failed:",e);
                throw new ApiException(HttpStatus.FORBIDDEN,"Authorization failed.");
            }


            // loads user details section
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) { // == null   if I am authenticated (not null), I do not need to continue with authentification
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
