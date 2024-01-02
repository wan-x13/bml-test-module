package com.bml.infrastructures.services;


import com.bml.domain.Role;
import com.bml.domain.User;
import com.bml.infrastructures.dtos.LoginDto;
import com.bml.infrastructures.dtos.UserDto;
import com.bml.infrastructures.exception.ConflictException;
import com.bml.infrastructures.repository.RoleRepository;
import com.bml.infrastructures.repository.UserRepository;
import com.bml.infrastructures.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public User save(User user) {
        if(userRepository.findAll().stream()
                .anyMatch(it->it.getUsername().equals(user.getUsername()))){
            throw new ConflictException("User already exists");
        }
        user.setPassword(passwordEncoder().encode(user.getPassword()));
        Role role = new Role();
        role.setName("ROLE_USER");
        roleRepository.save(role);
        user.setRoles(List.of(role));
        return userRepository.save(user);
    }
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public LoginDto login(UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getUsername(),
                        userDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return  new LoginDto(userDetails.getUsername(), jwt);
    }

    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
