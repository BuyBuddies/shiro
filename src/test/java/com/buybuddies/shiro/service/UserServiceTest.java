//package com.buybuddies.shiro.service;
//
//import com.buybuddies.shiro.data.user.UserDTO;
//import com.buybuddies.shiro.data.user.User;
//import com.buybuddies.shiro.data.user.UserMapper;
//import com.buybuddies.shiro.data.user.UserRepository;
//import com.google.api.gax.rpc.InvalidArgumentException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private UserService userService;
//
//    private UserDTO userDTO;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        userDTO = new UserDTO();
//        userDTO.setFirebaseUid("firebase-123");
//        userDTO.setEmail("test@example.com");
//        userDTO.setName("Test User");
//
//        user = new User();
//        user.setId(1L);
//        user.setFirebaseUid("firebase-123");
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//    }
//
//    @Test
//    void getUserByFirebaseUid_WhenUserExists_ShouldReturnUserDTO() {
//        // given
//        when(userRepository.findByFirebaseUid("firebase-123")).thenReturn(Optional.of(user));
//
//        // when
//        UserDTO result = userService.getUserByFirebaseUid("firebase-123");
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getFirebaseUid()).isEqualTo("firebase-123");
//        assertThat(result.getEmail()).isEqualTo("test@example.com");
//        assertThat(result.getName()).isEqualTo("Test User");
//    }
//
//    @Test
//    void getUserByFirebaseUid_WhenUserDoesNotExist_ShouldReturnNull() {
//        // when
//        when(userRepository.findByFirebaseUid("firebase-123")).thenReturn(Optional.empty());
//
//        // given
//        UserDTO result = userService.getUserByFirebaseUid("firebase-123");
//
//        // then
//        assertThat(result).isNull();
//    }
//
//    @Test
//    void createUser_WhenValidUserDTO_ShouldReturnCreatedUser() {
//        // When
//        // Mock the repository.save() method
//        // any(User.class) means "any User object passed to save()"
//        // We return our pre-built 'user' object (simulating database save)
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        // Given
//        UserDTO result = userService.createUser(userDTO);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.()).isEqualTo("firebase-123");
//
//        // verify() checks that a method was called on our mock
//        // This ensures the service actually tries to save to the database
//        // We use any(User.class) because we don't care about the exact object,
//        // just that save() was called with some User object
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    void createUser_WhenEmailAlreadyExists_ShouldThrowException() {
//        // Given
//        when(userRepository.existsByEmail("firebase-123")).thenReturn(true);
//
//        // When & Then
//        InvalidArgumentException thrownException = Assertions.assertThrows(
//          InvalidArgumentException.class,
//          () -> userService.createUser(userDTO)
//        );
//
//    }
//}
