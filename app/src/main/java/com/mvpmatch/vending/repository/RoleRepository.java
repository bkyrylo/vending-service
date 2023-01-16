package com.mvpmatch.vending.repository;

import com.mvpmatch.vending.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("select r from Role r where r.roleName in :roleNames")
    List<Role> findByRoleNames(@Param("roleNames") List<String> roleNames);
}