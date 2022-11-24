package com.example.project.mindray.oidc.rolemanage.repository;

import com.example.project.mindray.oidc.rolemanage.domain.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {
    List<UserRole> findByAuthorId(int authorId);

    void deleteAllByAuthorId(int authorId);

    Optional<UserRole> findByRolename(String name);
}
