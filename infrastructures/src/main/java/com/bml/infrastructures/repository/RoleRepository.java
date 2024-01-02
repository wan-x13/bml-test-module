package com.bml.infrastructures.repository;

import com.bml.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;



public interface RoleRepository extends JpaRepository<Role, Long> {
}
