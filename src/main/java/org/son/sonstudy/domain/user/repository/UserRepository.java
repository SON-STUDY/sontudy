package org.son.sonstudy.domain.user.repository;

import org.son.sonstudy.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsUserByEmail(String email);

    Optional<User> findByEmail(String email);
}
