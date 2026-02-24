package com.nick404s.dailyfocus.config;

import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // runs once per HTTP request

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Lazy - delays the creation/injection of the bean until it's needed
    // to avoid a circular dependency. adds efficiency
    public JwtAuthenticationFilter(JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    // runs once per request
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, // the data needs to have values
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // get the header out of the request
        final String authHeader = request.getHeader("Authorization");

        // check the header
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            // reject the request
            filterChain.doFilter(request,response);
            return;
        }

        // get the token which starts at the 7 char after "Bearer "
        final String jwToken = authHeader.substring(7);
        // get the email
        final String userEmail = jwtService.extractUsername(jwToken);

        // check the email and sec context holder
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // load the user details from the db
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // cast to User to access active status
            User user = (User) userDetails;

            // check for inactive user to reject authorization
            if (!user.isActive()){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Account is deactivated\"}");
                return;
            }

            // validate the token
            if (jwtService.isTokenValid(jwToken, userDetails)){
                // create a new auth token
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // credentials null because of using jwt
                        userDetails.getAuthorities() // all user permissions
                );

                // add the details to the auth token
                authenticationToken
                        .setDetails(new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                // set the user to the spring security
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authenticationToken);

            }

            // go on with the filtering
            filterChain.doFilter(request, response);
        }
    }
}
