package io.github.aplaraujo.repositories;

import io.github.aplaraujo.entities.Role;
import io.github.aplaraujo.entities.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthority(RoleEnum authority);
}
