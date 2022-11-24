package com.example.project.mindray.oidc.rolemanage.controller;

import com.example.framework.common.exception.ThereIsNoRoleWithThisNameException;
import com.example.project.mindray.oidc.rolemanage.domain.Role;
import com.example.project.mindray.oidc.rolemanage.dto.RoleDTO;
import com.example.project.mindray.oidc.rolemanage.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;

    public RoleController(final RoleRepository roleRepository, final ObjectMapper objectMapper) {
        this.roleRepository = roleRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<Role>> findAll() {
        List<Role> roles = StreamSupport.stream(
                this.roleRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
        List<Role> l = new ArrayList<>();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> findById(@PathVariable int id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role with id = " + id + " not found"
                ));
        return ResponseEntity.ok(role);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
        Role role = null;
        Iterator iterator =  roleRepository.findAll().iterator();

        while (iterator.hasNext()) {
            Role _r = (Role) iterator.next();
            if("1".equals(_r.getIsdefault())){
                role = _r;
                break;
            }
        }
        if(role == null){
           throw  new ThereIsNoRoleWithThisNameException("There is no role named " + name);
        }
        /*
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ThereIsNoRoleWithThisNameException("There is no role named " + name));

         */
        return ResponseEntity.ok(role);
    }

    /**
     * 新增role
     * @param roles
     * @return
     */
    @PostMapping("/")
    public ResponseEntity<Role> create(@RequestBody List<Role> roles) {
        StreamSupport.stream(roles.spliterator(),false)
                .map(role -> this.roleRepository.save(role))
                .collect(Collectors.toList());
        return new ResponseEntity<Role>(
                new Role("roles"),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Role role) {
        //checkName(role.getName());
        this.roleRepository.save(role);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Role> changeSomeFields(@PathVariable int id, @RequestBody RoleDTO patch) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role with id = " + id + " not found"
                ));
        String name = patch.getName();
        if (name != null) {
            checkName(name);
            role.setName(name);
        }
        return ResponseEntity.ok(roleRepository.save(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Role role = new Role();
        role.setId(id);
        this.roleRepository.delete(role);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(ThereIsNoRoleWithThisNameException.class)
    public void handleException(Exception e, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        resp.setContentType("application/json");
        resp.getWriter().write(objectMapper.writeValueAsString(
                new HashMap<>() { {
                    put("message", e.getMessage());
                    put("type", e.getClass());
                } }));
    }

    private void checkName(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            throw new IllegalArgumentException("Name of role must not be empty");
        }

        if (roleRepository.findByName(roleName).isPresent()) {
            throw new IllegalArgumentException("Name of role " + roleName + " already exists");
        }
    }
}
