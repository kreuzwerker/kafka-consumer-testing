package de.kreuzwerker.blogs.kafkaconsumertesting.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByUserId(String userId);

    Optional<UserEntity> findUserEntityByPid(Long pId);
}
