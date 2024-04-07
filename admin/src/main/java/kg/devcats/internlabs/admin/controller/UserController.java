package kg.devcats.internlabs.admin.controller;

import kg.devcats.internlabs.admin.dto.response.UserDTO;
import kg.devcats.internlabs.admin.service.UserService;
import kg.devcats.internlabs.core.entity.Role;
import kg.devcats.internlabs.core.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<Page<User>> getUsers(
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size,
                                            @RequestParam(defaultValue = "username") String sortField,
                                            @RequestParam(defaultValue = "asc") String sortOrder,
                                            @RequestParam(required = false) String usernamePrefix,
                                            @RequestParam(required = false) String emailPrefix,
                                            @RequestParam(required = false) Integer roleId) {
        Page<User> usersPage;
        if ((usernamePrefix != null && !usernamePrefix.isEmpty()) || (emailPrefix != null && !emailPrefix.isEmpty()) || roleId != null) {
            usersPage = userService.findByFilters(usernamePrefix, emailPrefix, roleId, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortField));
        } else {
            usersPage = userService.getUsers(page, size, sortField, sortOrder);
        }

        if (usersPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        UserDTO userData = userService.getUserById(id);
        if (userData != null) {
            return new ResponseEntity<>(userData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/password/edit")
    @PreAuthorize("hasAuthority('account.read')")
    public ResponseEntity<String> changeUserPassword(@PathVariable("id") Long id,
                                                    @RequestParam String oldPassword,
                                                    @RequestParam String newPassword) {
       try{
           userService.changeUserPassword(id, oldPassword, newPassword);
           return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
       } catch (NoSuchElementException e){
           return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
       } catch (IllegalArgumentException e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
       }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user.create')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        Set<Role> roles = user.getRoles();
        Integer roleId = roles != null && !roles.isEmpty() ? roles.iterator().next().getId() : null;
        if (roleId != null) {
            if (roleId == 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (roleId == 2) {
                if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("admin.create"))) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        User userData = userService.createUser(user);
        return new ResponseEntity<>(userData, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user.create')")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User updatedUser) {
        UserDTO oldUserData = userService.getUserById(id);
        Role oldRole = oldUserData.role().iterator().next();
        Integer oldRoleId = oldRole.getId();
        Set<Role> roles = updatedUser.getRoles();
        Integer newRoleId = null;
        if (roles != null && !roles.isEmpty()) {
            Role role = roles.iterator().next();
            newRoleId = role.getId();
        }
        if (newRoleId == 1 || oldRoleId == 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (newRoleId == 2 || oldRoleId == 2) {
            if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("admin.create"))) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        User userData = userService.updateUser(id, updatedUser);
        if (userData != null) {
            return new ResponseEntity<>(userData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user.delete')")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        UserDTO userData = userService.getUserById(id);
        if (userData == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Role role = userData.role().iterator().next();
        Integer roleId = role.getId();
        if (roleId != null) {
            if (roleId == 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (roleId == 2) {
                if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("admin.delete"))) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
