package com.example.competr.portal.repository;

import com.example.competr.portal.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, String> {

    Optional<PlayerEntity> findByPhoneNumber(String phoneNumber);
    Optional<PlayerEntity> findByUserName(String userName);

    @Query("SELECT p.userName FROM PlayerEntity p WHERE LOWER(p.userName) LIKE LOWER(CONCAT('%', :playerName, '%'))")
    List<String> findUserNamesByUserNameContainingIgnoreCase(@Param("playerName") String playerName);


}
