//package com.example.competr.portal.controller;
//
//import com.example.competr.portal.service.PlayerService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//@RequestMapping("/players")
//public class PlayerController {
//
//
//    private final PlayerService playerService;
//
//
//    @GetMapping
//    public ResponseEntity<List<User>> searchPlayers(@RequestParam(required = false) String search) {
//        List<User> players;
//        if (search == null || search.isEmpty()) {
//            players = playerService.getAllPlayers();
//        } else {
//            players = playerService.searchPlayersByName(search);
//        }
//        return ResponseEntity.ok(players);
//    }
//}
//
