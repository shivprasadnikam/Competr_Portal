//package com.example.competr.portal.serviceImpl;
//
//import com.example.competr.portal.service.PlayerService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PlayerServiceImpl implements PlayerService {
//    private final UserRepository userRepository;
//
//    public List<User> getAllPlayers() {
//        return userRepository.findAll();
//    }
//
//    public List<User> searchPlayersByName(String name) {
//        return userRepository.findByUserNameContainingIgnoreCase(name);
//    }
//}
