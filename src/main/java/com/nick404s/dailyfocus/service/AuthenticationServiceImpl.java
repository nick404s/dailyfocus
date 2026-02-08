package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.model.Authority;
import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationServiceImpl  implements AuthenticationService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // changes the db state
    public void register(RegisterRequest registerRequest) throws Exception {

        // check for a duplicate email
        if (isEmailPresent(registerRequest.getEmail())){
            throw new Exception("Email already taken");
        }

        // create a new user
        User user = createNewUser(registerRequest);

        // save the user to the db
        userRepository.save(user);
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
        authorityList.add(new Authority("ROLE_USER"));

        boolean isFirstUser = userRepository.count() == 0;

        // check if the user is the first one in the db
        if (isFirstUser){
            // make the first user an administrator by default
            authorityList.add(new Authority("ROLE_ADMIN"));
        }

        return authorityList;
    }
}
