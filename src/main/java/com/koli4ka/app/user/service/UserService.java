package com.koli4ka.app.user.service;

import com.koli4ka.app.exeption.EmailAlreadyExists;
import com.koli4ka.app.exeption.PhoneNumberAlreadyExists;
import com.koli4ka.app.exeption.UserNameAlreadyExists;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.web.dtos.EditUserDTO;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.repository.UserRepository;
import com.koli4ka.app.web.dtos.RegisterRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        Optional<User> userRepositoryByUsername=userRepository.findByUsername(registerRequest.getUsername());
        Optional<User>userRepositoryByEmail =userRepository.getByEmail(registerRequest.getEmail());
        Optional<User>userRepositoryByPhoneNumber=userRepository.getByPhone(registerRequest.getPhoneNumber());



        if(userRepositoryByUsername.isPresent()) {
            throw new UserNameAlreadyExists("Username already exists");
        } if(userRepositoryByEmail.isPresent()) {
            throw new EmailAlreadyExists("Email already exists");
        } if(userRepositoryByPhoneNumber.isPresent()) {
            throw new PhoneNumberAlreadyExists("Phone number already exists");
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
       throw new UsernameNotFoundException("Username not found");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
       return new AuthenticationDetails(user.getId(), user.getUsername(), user.getPassword(), user.getRole());


    }

    public User getById(UUID id) {
        return userRepository.getUserById(id);
    }

    public List<User> getChatInfo(List<UUID> uuids) {
        List<User> users=new ArrayList<>();
        uuids.forEach(uuid->{
            User user=userRepository.getUserById(uuid);
            if(user!=null) {
                users.add(user);            }

        });
        return users;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void changeRole(UUID id) {

        User user=userRepository.getUserById(id);
        if(user.getRole()==UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        }
        else {
            user.setRole(UserRole.USER);
        }
        userRepository.save(user);


    }

    public User findByEmail(String email) {
        return userRepository.getByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public void updateProfile(UUID userId, EditUserDTO editUserDTO) {
        User user = userRepository.getUserById(userId);
        
        // Update user information
        user.setFirstName(editUserDTO.getFirstName());
        user.setLastName(editUserDTO.getLastName());
        user.setImageUrl(editUserDTO.getImageUrl());

        userRepository.save(user);
    }
}
