package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.UserDTOMapper;
import kg.devcats.internlabs.admin.dto.response.UserDTO;
import kg.devcats.internlabs.core.entity.Role;
import kg.devcats.internlabs.core.entity.User;
import kg.devcats.internlabs.core.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;

    public UserService(PasswordEncoder encoder, UserRepository userRepository, UserDTOMapper userDTOMapper) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
    }

    public Page<User> getUsers(int pageNumber, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return userRepository.findAll(pageRequest);
    }

    public Page<User> findByFilters(String usernamePrefix, String emailPrefix, Integer roleId, PageRequest pageable) {
        return userRepository.findByFilters(usernamePrefix, emailPrefix, roleId, pageable);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null ? userDTOMapper.apply(user) : null;
    }

    public User createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        boolean existsByUsername = userRepository.existsByUsername(user.getUsername());
        boolean existsByEmail = userRepository.existsByEmail(user.getEmail());
        if(existsByUsername || existsByEmail){
            throw new DataIntegrityViolationException("username - " + user.getUsername()  + " or email -" +  user.getEmail() + " already exists.");
        }
        return userRepository.save(user);
    }

    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser != null) {
            if (updatedUser.getUsername() != null) {
                existingUser.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail()); // TODO refactor
            }
            if (updatedUser.getPassword() != null) {
                existingUser.setPassword(encoder.encode(updatedUser.getPassword()));
            }
            Set<Role> roles = updatedUser.getRoles();
            Integer roleId = roles != null && !roles.isEmpty() ? roles.iterator().next().getId() : null; // TODO refactor
            if (roleId != null) {
                existingUser.setRoles(roles);
            }
            return userRepository.save(existingUser);
        }
        return null;
    }

    public void delete(Long id) {
        userRepository.delete(userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found")));
    }

    public void changeUserPassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

}