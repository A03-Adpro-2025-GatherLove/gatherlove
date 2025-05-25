package id.ac.ui.cs.advprog.gatherlove.authentication.service;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserEntity getUserById(UUID userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        return user.orElse(new UserEntity());
    }
}

