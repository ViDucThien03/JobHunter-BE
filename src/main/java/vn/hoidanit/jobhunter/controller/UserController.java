package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.DTO.ResCreateUserDTO;
import vn.hoidanit.jobhunter.DTO.ResUserDTO;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.error.IdInvalidException;
import vn.hoidanit.jobhunter.model.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create new a User success")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isExitsEmail = this.userService.existsByEmail(user.getEmail());
        if (isExitsEmail) {
            throw new IdInvalidException("Email " + user.getEmail() + " already exists!");
        }
        String password = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.getUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("Not found User");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.getUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("Not found User");
        }
        User user = this.userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("get all users successed")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> specification,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUser(specification, pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<ResCreateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {

        User currentUser = this.userService.updateUser(user);
        if (currentUser == null) {
            throw new IdInvalidException("User does not exist");
        }
        return ResponseEntity.ok(this.userService.convertToResCreateUserDTO(currentUser));
    }
}
