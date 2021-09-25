package com.ap.greenpole.usermodule.repository;

import com.ap.greenpole.usermodule.model.UserPermission;
import com.ap.greenpole.usermodule.model.UserPermission;
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
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long>, PagingAndSortingRepository<UserPermission, Long> {

    @Query(value = "SELECT up.id as id, permission_id, user_id, p.value as value FROM user_permissions up INNER JOIN permissions p ON p.id = up.permission_id WHERE value = ?1", nativeQuery = true)
    Optional<UserPermission> findByValue(String value);

    @Query(value = "SELECT up.id as id, permission_id, user_id, p.value as value FROM user_permissions up INNER JOIN permissions p ON p.id = up.permission_id WHERE up.id = ?1", nativeQuery = true)
    Optional<UserPermission> findById(long id);

    @Query(value = "SELECT up.id as id, permission_id, user_id, p.value as value FROM user_permissions up INNER JOIN permissions p ON p.id = up.permission_id WHERE value = ?1 AND user_id = ?2", nativeQuery = true)
    Optional<UserPermission> findByValueAndUserId(String value, long userId);

    @Query(value = "SELECT up.id as id, permission_id, user_id, p.value as value FROM user_permissions up INNER JOIN permissions p ON p.id = up.permission_id WHERE user_id = ?1", nativeQuery = true)
    List<UserPermission> findAllByUserId(long userId);

}
