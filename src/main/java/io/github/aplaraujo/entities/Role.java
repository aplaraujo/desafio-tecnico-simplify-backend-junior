package io.github.aplaraujo.entities;

import io.github.aplaraujo.entities.enums.RoleEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private RoleEnum authority;
}
