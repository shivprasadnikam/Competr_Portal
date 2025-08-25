package com.example.competr.portal.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CreateMatchService {
    boolean createMatch(List<String> players, String matchType);
}
