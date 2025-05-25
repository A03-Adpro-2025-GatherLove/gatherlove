package id.ac.ui.cs.advprog.gatherlove.authentication.service;

import java.util.UUID;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;

public interface UserService {
    UserEntity getUserById(UUID userId);
}
