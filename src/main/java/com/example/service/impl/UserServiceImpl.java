package com.example.service.impl;

import com.example.dto.user.UserRegistrationRequestDto;
import com.example.dto.user.UserResponseDto;
import com.example.exception.EntityNotFoundException;
import com.example.exception.RegistrationException;
import com.example.mapper.UserMapper;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.role.RoleRepository;
import com.example.repository.user.UserRepository;
import com.example.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("User already exists");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoles(Set.of(roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException("Can't find role by role name"
                        + Role.RoleName.USER.name()))));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getShippingAddress() != null) {
            user.setShippingAddress(request.getShippingAddress());
        }
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }
}
