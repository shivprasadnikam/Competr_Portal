package com.example.competr.portal.repository;

import com.example.competr.portal.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    Optional<PlayerEntity> findByPhoneNumber(String phoneNumber);
    Optional<PlayerEntity> findByUserName(String userName);

}
