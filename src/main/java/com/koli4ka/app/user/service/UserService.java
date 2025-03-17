package com.koli4ka.app.user.service;

import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.repository.UserRepository;
import com.koli4ka.app.web.dtos.LoginRequest;
import com.koli4ka.app.web.dtos.RegisterRequest;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest registerRequest) {

        Optional<User> optionalUser=userRepository.findByUsername(registerRequest.getUsername());

        if(optionalUser.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhoneNumber())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .imageUrl(registerRequest.getImageUrl())
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

    }




    public User getByUsername (String username) {
       Optional<User> optionalUser=userRepository.findByUsername(username);
       if(optionalUser.isPresent()) {
           return optionalUser.get();
       }
       throw new RuntimeException("Username not found");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
       return new AuthenticationDetails(user.getId(), user.getUsername(), user.getPassword(), user.getRole());


    }

    public User getById(UUID id) {
        return userRepository.getUserById(id);
    }
}
