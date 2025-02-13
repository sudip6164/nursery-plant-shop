package com.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nursery.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	 Role findByName(String name);
}
