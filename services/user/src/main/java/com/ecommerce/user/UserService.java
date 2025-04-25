package com.ecommerce.user;

import com.ecommerce.authorization.Role;
import com.ecommerce.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    /**
     * Finds a User entity by its id.
     *
     * @param id the identifier of the User.
     * @return the found User entity.
     * @throws UserNotFoundException if no User with the provided id is found.
     */
    @Transactional(readOnly = true)
    public User findUserEntityById(Long id) {
        Objects.requireNonNull(id, "ID uživatele nesmí být prázdné.");
        log.debug("Fetching user with id: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Uživatel s ID %s nebyl nalezen.", id)
                ));
    }

    /**
     * Retrieves a User by id and maps it to a UserResponse DTO.
     *
     * @param id the identifier of the User.
     * @return the mapped UserResponse.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        Objects.requireNonNull(id, "ID uživatele nesmí být prázdné.");
        log.debug("Fetching user with id: {}", id);

        User user = this.findUserEntityById(id);
        return userMapper.toResponse(user);
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param request the pagination information.
     * @return a page of UserResponse DTOs.
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(PageRequest request) {
        Objects.requireNonNull(request, "Požadavek na stránkování být prázdný.");
        log.debug("Fetching users with pagination parameters: {}", request);
        return userRepository.findAll(request)
                .map(userMapper::toResponse);
    }

    @Transactional
    public void updateUserRole(Long id, String role) {
        Objects.requireNonNull(id, "ID uživatele nesmí být prázdné.");
        Objects.requireNonNull(role, "Role nesmí být prázdná.");
        log.debug("Fetching user with id: {}", id);

        User user = this.findUserEntityById(id);
        user.setRole(Role.valueOf(role));

        userRepository.save(user);
        log.info("Role {} was successfully assigned to user with ID: {}", role, id);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to be deleted, must not be null
     * @throws NullPointerException if the provided ID is null
     * @throws UserNotFoundException if the user does not exist
     *
     */
    @Transactional
    public void deleteUserById(Long id) {
        Objects.requireNonNull(id, "ID uživatele nesmí být prázdné.");
        log.debug("Deleting user with id: {}", id);

        User user = this.findUserEntityById(id);

        userRepository.delete(user);
        log.info("User with id: {} has been deleted successfully.", id);
    }
}
