package com.buybuddies.shiro.service;


import com.buybuddies.shiro.dto.HomeDTO;
import com.buybuddies.shiro.dto.UserDTO;
import com.buybuddies.shiro.entity.Home;
import com.buybuddies.shiro.entity.User;
import com.buybuddies.shiro.exception.ResourceNotFoundException;
import com.buybuddies.shiro.repository.HomeRepository;
import com.buybuddies.shiro.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final HomeRepository homeRepository;
    private final UserRepository userRepository;

    @Transactional
    public HomeDTO createHome(HomeDTO homeDTO) {
        User owner = userRepository.findById(homeDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Home home = new Home();
        home.setName(homeDTO.getName());
        home.setDescription(homeDTO.getDescription());
        home.setOwner(owner);

        if (homeDTO.getMemberIds() != null && !homeDTO.getMemberIds().isEmpty()) {
            Set<User> members = userRepository.findAllById(homeDTO.getMemberIds())
                    .stream()
                    .collect(Collectors.toSet());
            home.setMembers(members);
        }

        home = homeRepository.save(home);
        return convertToDTO(home);
    }

    public HomeDTO getHome(Long homeId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new RuntimeException("Home not found"));
        return convertToDTO(home);
    }

    public List<HomeDTO> getAllHomes() {
        return homeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<HomeDTO> getHomesByOwner(Long ownerId) {
        return homeRepository.findByOwnerId(ownerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<HomeDTO> getHomesByMember(Long userId) {
        return homeRepository.findByMembersId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public HomeDTO updateHome(Long id, HomeDTO homeDTO) {
        Home home = homeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Home not found"));

        home.setName(homeDTO.getName());
        home.setDescription(homeDTO.getDescription());

        if (homeDTO.getOwnerId() != null && !homeDTO.getOwnerId().equals(home.getOwner().getId())) {
            User newOwner = userRepository.findById(homeDTO.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("New owner not found"));
            home.setOwner(newOwner);
        }

        if (homeDTO.getMemberIds() != null) {
            Set<User> members = userRepository.findAllById(homeDTO.getMemberIds())
                    .stream()
                    .collect(Collectors.toSet());
            home.setMembers(members);
        }

        home = homeRepository.save(home);
        return convertToDTO(home);
    }

    @Transactional
    public void deleteHome(Long id) {
        if (!homeRepository.existsById(id)) {
            throw new RuntimeException("Home not found");
        }
        homeRepository.deleteById(id);
    }

    @Transactional
    public HomeDTO addMember(Long homeId, Long userId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new RuntimeException("Home not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        home.getMembers().add(user);
        home = homeRepository.save(home);
        return convertToDTO(home);
    }

    @Transactional
    public HomeDTO removeMember(Long homeId, Long userId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new RuntimeException("Home not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        home.getMembers().remove(user);
        home = homeRepository.save(home);
        return convertToDTO(home);
    }

    private HomeDTO convertToDTO(Home home) {
        HomeDTO dto = new HomeDTO();
        dto.setId(home.getId());
        dto.setName(home.getName());
        dto.setDescription(home.getDescription());
        dto.setOwnerId(home.getOwner().getId());
        dto.setMemberIds(home.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return dto;
    }
}