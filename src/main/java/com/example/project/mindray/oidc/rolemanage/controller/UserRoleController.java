package com.example.project.mindray.oidc.rolemanage.controller;

import com.example.project.mindray.oidc.rolemanage.domain.Person;
import com.example.project.mindray.oidc.rolemanage.domain.UserRole;
import com.example.project.mindray.oidc.rolemanage.repository.UserRoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@RestController
@RequestMapping("/userrole")
public class UserRoleController {
    private final UserRoleRepository userRoleRepository;
    private final RestTemplate restTemplate;

    public UserRoleController(UserRoleRepository userRoleRepository, RestTemplate restTemplate) {
        this.userRoleRepository = userRoleRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserRole>> findAll() {
        List<UserRole> userRole = StreamSupport.stream(userRoleRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userRole);
    }


    /**
     * 按用户ID，获取该用户的权限集合
     * @param authorId
     * @return
     */
    @GetMapping("/authorId/{authorId}")
    public ResponseEntity<List<UserRole>> findAllCreatedByUser(@PathVariable int authorId) {

        return ResponseEntity.ok(userRoleRepository.findByAuthorId(authorId));
    }

    /**
     * 新增用户权限关系记录
     * @param userRole
     * @return
     */
    @PostMapping("/")
    public ResponseEntity<UserRole> create(@RequestBody UserRole userRole) {
        return new ResponseEntity<>(
                userRoleRepository.save(userRole),
                HttpStatus.CREATED
        );
    }
}
