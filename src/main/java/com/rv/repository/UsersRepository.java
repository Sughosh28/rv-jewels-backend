package com.rv.repository;

import com.rv.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);

    @Query("SELECT u.id FROM UserEntity u WHERE u.username = ?1")
    Integer findIdByUsername(String username);

    UserEntity findByUsername(String username);
}
