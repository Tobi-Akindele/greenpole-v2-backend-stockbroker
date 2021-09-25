package com.ap.greenpole.usermodule.repository;

import com.ap.greenpole.usermodule.model.Permission;
import com.ap.greenpole.usermodule.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 18-Aug-20 04:51 AM
 */
@Transactional
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, PagingAndSortingRepository<Permission, Long> {

    @Query(value = "SELECT * FROM permissions WHERE value = ?1", nativeQuery = true)
    Optional<Permission> findByValue(String value);

    @Query(value = "SELECT * FROM permissions WHERE value = ?1", nativeQuery = true)
    List<Permission> findAllByValue(String value);

}
