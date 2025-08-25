package com.example.competr.portal.controller;

import com.example.competr.portal.entity.PlayerEntity;
import com.example.competr.portal.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/{userName}")
    public ResponseEntity<PlayerEntity> getProfile(@PathVariable String userName) {
        Optional<PlayerEntity> playerOpt = playerRepository.findByUserName(userName);
        
        if (playerOpt.isPresent()) {
            PlayerEntity player = playerOpt.get();
            // Remove password from response for security
            player.setPassword(null);
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
