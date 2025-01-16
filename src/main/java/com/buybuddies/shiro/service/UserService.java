package com.buybuddies.shiro.service;

import com.buybuddies.shiro.dto.UserDTO;
import com.buybuddies.shiro.entity.User;
import com.buybuddies.shiro.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setFirebaseUid(userDTO.getFirebaseUid());

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    public UserDTO getUserByFirebaseUid(String firebaseUid){
        Optional<User> userOptional = userRepository.findByFirebaseUid(firebaseUid);
        if (userOptional.isPresent()) {
            return convertToDTO(userOptional.get());
        } else {
            return null;
        }
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(String firebaseUid, UserDTO userDTO) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteUserByFirebaseUid(String firebaseUid) {
        if (!userRepository.existsByFirebaseUid(firebaseUid)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteByFirebaseUid(firebaseUid);
    }


    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setFirebaseUid(user.getFirebaseUid());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());

        return dto;
    }
}
