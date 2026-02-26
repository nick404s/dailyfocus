package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.dto.request.AuthenticationRequest;
import com.nick404s.dailyfocus.dto.response.AuthenticationResponse;
import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.dto.request.RegisterRequest;
import com.nick404s.dailyfocus.util.AppRoles;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AuthenticationServiceImpl  implements AuthenticationService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional // allows making changes the db state
    public void register(RegisterRequest registerRequest) {

        // check for a duplicate email
        if (isEmailPresent(registerRequest.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The email already registered");
        }

        // create a new user
        User user = createNewUser(registerRequest);

        // save the user to the db
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true) // restricts changes to the db
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {

        // validate the authentication
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );

        // get the user
        User user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        // create a jwt token for the user
        String jwToken = jwtService.generateToken(new HashMap<>(), user);

        return new AuthenticationResponse(jwToken);
    }

    private boolean isEmailPresent(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    private User createNewUser(RegisterRequest registerRequest) {

        User user = new User();

        // set the user id to 0 because the db will assign the id
        user.setId(0);

        // assign the other fields
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());

        // encode the password
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // set initial authorities
        user.setAuthorities(getInitialAuthority());

        return user;
    }

    private List<Authority> getInitialAuthority(){

        List<Authority> authorityList = new ArrayList<>();

        // assign the default role
        authorityList.add(new Authority(AppRoles.ROLE_USER));

        boolean isFirstUser = userRepository.count() == 0;

        // check if the user is the first one in the db
        if (isFirstUser){
            // make the first user an administrator by default
            authorityList.add(new Authority(AppRoles.ROLE_ADMIN));
        }

        return authorityList;
    }
}
