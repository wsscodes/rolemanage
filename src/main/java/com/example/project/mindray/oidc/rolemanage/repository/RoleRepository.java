package com.example.project.mindray.oidc.rolemanage.repository;

import com.example.project.mindray.oidc.rolemanage.domain.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}