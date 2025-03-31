package com.koli4ka.app.user.repository;

import com.koli4ka.app.user.model.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    User getUserById(UUID id);

    Optional<User> getByEmail(String email);

    Optional<User> getByPhone(String phone);

    User findByEmail(String email);
}
