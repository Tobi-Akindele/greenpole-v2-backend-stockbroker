package com.ap.greenpole.usermodule.repository;

import com.ap.greenpole.usermodule.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 18-Aug-20 04:51 AM
 */
public interface UserRoleRepository extends JpaRepository<UserRole, Long>, PagingAndSortingRepository<UserRole, Long> {

    @Query(value = "SELECT ur.id as id, role_id, user_id, r.value as value FROM user_roles ur INNER JOIN roles r ON r.id = ur.role_id WHERE value = ?1", nativeQuery = true)
    Optional<UserRole> findByValue(String value);

    @Query(value = "SELECT ur.id as id, role_id, user_id, r.value as value FROM user_roles ur INNER JOIN roles r ON r.id = ur.role_id WHERE ur.id = ?1", nativeQuery = true)
    Optional<UserRole> findById(long id);

    @Query(value = "SELECT ur.id as id, role_id, user_id, r.value as value FROM user_roles ur INNER JOIN roles r ON r.id = ur.role_id WHERE value = ?1 AND user_id = ?2", nativeQuery = true)
    Optional<UserRole> findByValueAndUserId(String value, long userId);

    @Query(value = "SELECT ur.id as id, role_id, user_id, r.value as value FROM user_roles ur INNER JOIN roles r ON r.id = ur.role_id WHERE user_id = ?1", nativeQuery = true)
    List<UserRole> findAllByUserId(long userId);

}
