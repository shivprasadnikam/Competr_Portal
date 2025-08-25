package com.example.competr.portal.controller;

import com.example.competr.portal.dto.*;
import com.example.competr.portal.entity.ChallengeEntity;
import com.example.competr.portal.entity.MatchEntity;
import com.example.competr.portal.repository.ChallengeRepository;
import com.example.competr.portal.repository.MatchRepository;
import com.example.competr.portal.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping
    public ResponseEntity<List<ChallengeResponse>> getAllChallenges() {
        try {
            String currentUser = getCurrentUser();
            List<ChallengeEntity> challenges = challengeRepository.findAllChallengesForPlayer(currentUser);
            
            List<ChallengeResponse> response = challenges.stream()
                .map(challenge -> convertToResponse(challenge, currentUser))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/sent")
    public ResponseEntity<List<ChallengeResponse>> getSentChallenges() {
        try {
            String currentUser = getCurrentUser();
            List<ChallengeEntity> challenges = challengeRepository.findSentChallenges(currentUser);
            
            List<ChallengeResponse> response = challenges.stream()
                .map(challenge -> convertToResponse(challenge, currentUser))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/received")
    public ResponseEntity<List<ChallengeResponse>> getReceivedChallenges() {
        try {
            String currentUser = getCurrentUser();
            List<ChallengeEntity> challenges = challengeRepository.findReceivedChallenges(currentUser);
            
            List<ChallengeResponse> response = challenges.stream()
                .map(challenge -> convertToResponse(challenge, currentUser))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ChallengeActionResponse> createChallenge(@RequestBody CreateChallengeRequest request) {
        try {
            String currentUser = getCurrentUser();
            
            // Validation
            if (currentUser.equals(request.getTargetPlayer())) {
                return ResponseEntity.ok(new ChallengeActionResponse(
                    null, "error", "Cannot challenge yourself", null, null));
            }
            
            if (!playerRepository.findByUserName(request.getTargetPlayer()).isPresent()) {
                return ResponseEntity.ok(new ChallengeActionResponse(
                    null, "error", "Target player not found", null, null));
            }
            
            // Check for existing pending challenges
            List<ChallengeEntity> existingChallenges = challengeRepository
                .findPendingChallengesBetweenPlayers(currentUser, request.getTargetPlayer());
            
            if (!existingChallenges.isEmpty()) {
                return ResponseEntity.ok(new ChallengeActionResponse(
                    null, "error", "Pending challenge already exists between players", null, null));
            }
            
            // Create challenge
            String challengeId = "challenge_" + System.currentTimeMillis();
            
            ChallengeEntity challenge = new ChallengeEntity();
            challenge.setId(challengeId);
            challenge.setFromPlayer(currentUser);
            challenge.setToPlayer(request.getTargetPlayer());
            challenge.setMessage(request.getMessage());
            challenge.setMatchType(ChallengeEntity.MatchType.valueOf(request.getMatchType()));
            challenge.setScoreTarget(request.getScoreTarget());
            challenge.setStatus(ChallengeEntity.ChallengeStatus.PENDING);
            
            challengeRepository.save(challenge);
            
            return ResponseEntity.ok(new ChallengeActionResponse(
                challengeId, "created", "Challenge sent successfully", request.getTargetPlayer(), null));
            
        } catch (Exception e) {
            return ResponseEntity.ok(new ChallengeActionResponse(
                null, "error", "Failed to create challenge: " + e.getMessage(), null, null));
        }
    }

    @PutMapping("/{challengeId}/respond")
    public ResponseEntity<ChallengeActionResponse> respondToChallenge(
            @PathVariable String challengeId,
            @RequestBody RespondToChallengeRequest request) {
        
        try {
            String currentUser = getCurrentUser();
            Optional<ChallengeEntity> challengeOpt = challengeRepository.findById(challengeId);
            
            if (challengeOpt.isEmpty()) {
                return ResponseEntity.ok(new ChallengeActionResponse(
                    challengeId, "error", "Challenge not found", null, null));
            }
            
            ChallengeEntity challenge = challengeOpt.get();
            
            // Verify current user is the target of the challenge
            if (!challenge.getToPlayer().equals(currentUser)) {
                return ResponseEntity.ok(new ChallengeActionResponse(
                    challengeId, "error", "Not authorized to respond to this challenge", null, null));
            }
            
            // Update challenge status
            ChallengeEntity.ChallengeStatus newStatus = ChallengeEntity.ChallengeStatus.valueOf(request.getResponse());
            challenge.setStatus(newStatus);
            
            String matchId = null;
            
            // If accepted, create a match
            if (newStatus == ChallengeEntity.ChallengeStatus.ACCEPTED) {
                matchId = "match_" + System.currentTimeMillis();
                
                MatchEntity match = new MatchEntity();
                match.setMatchId(matchId);
                match.setPlayers(Arrays.asList(challenge.getFromPlayer(), challenge.getToPlayer()));
                match.setMatchType(MatchEntity.MatchType.valueOf(challenge.getMatchType().toString()));
                match.setScoreTarget(challenge.getScoreTarget());
                match.setStatus(MatchEntity.MatchStatus.CREATED);
                match.setScores(Arrays.asList(0, 0));
                
                matchRepository.save(match);
                challenge.setMatchId(matchId);
            }
            
            challengeRepository.save(challenge);
            
            return ResponseEntity.ok(new ChallengeActionResponse(
                challengeId, request.getResponse(), "Challenge response recorded", null, matchId));
            
        } catch (Exception e) {
            return ResponseEntity.ok(new ChallengeActionResponse(
                challengeId, "error", "Failed to respond to challenge: " + e.getMessage(), null, null));
        }
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<ChallengeActionResponse> cancelChallenge(@PathVariable String challengeId) {
        try {
            String currentUser = getCurrentUser();
            Optional<ChallengeEntity> challengeOpt = challengeRepository.findById(challengeId);
            
            if (challengeOpt.isEmpty()) {
                return ResponseEntity.ok(new ChallengeActionResponse(
                    challengeId, "error", "Challenge not found", null, null));
            }
            
            ChallengeEntity challenge = challengeOpt.get();
            
            // Verify current user is the sender of the challenge
            if (!challenge.getFromPlayer().equals(currentUser)) {
                return ResponseEntity.ok(new ChallengeActionResponse(
                    challengeId, "error", "Not authorized to cancel this challenge", null, null));
            }
            
            // Update status to cancelled
            challenge.setStatus(ChallengeEntity.ChallengeStatus.CANCELLED);
            challengeRepository.save(challenge);
            
            return ResponseEntity.ok(new ChallengeActionResponse(
                challengeId, "cancelled", "Challenge cancelled successfully", null, null));
            
        } catch (Exception e) {
            return ResponseEntity.ok(new ChallengeActionResponse(
                challengeId, "error", "Failed to cancel challenge: " + e.getMessage(), null, null));
        }
    }

    private ChallengeResponse convertToResponse(ChallengeEntity challenge, String currentUser) {
        return new ChallengeResponse(
            challenge.getId(),
            challenge.getFromPlayer(),
            challenge.getToPlayer(),
            challenge.getFromPlayer().equals(currentUser),
            challenge.getMessage(),
            challenge.getMatchType().toString(),
            challenge.getScoreTarget(),
            challenge.getStatus().toString(),
            challenge.getCreatedAt(),
            challenge.getUpdatedAt()
        );
    }
}
