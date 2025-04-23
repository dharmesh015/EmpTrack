package com.ecom.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecom.entity.Role;

@Repository
public interface RoleDao extends CrudRepository<Role, String> {
	Optional<Role> findByRoleName(String roleName);
}
