package com.ap.greenpole.usermodule.repository;

import com.ap.greenpole.usermodule.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 18-Aug-20 04:49 AM
 */
@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long>  {

    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT (usr.*) FROM user_permissions user_perm INNER JOIN permissions perm" +
            " ON user_perm.permission_id = perm.id INNER JOIN users usr" +
            " ON  usr.id = user_perm.user_id WHERE perm.value = ?1", nativeQuery = true)
    List<User> findAllByPermission(String permission);

    @Query(value = "SELECT (usr.*) FROM user_roles user_perm INNER JOIN roles perm" +
            " ON user_perm.role_id = perm.id INNER JOIN users usr" +
            " ON  usr.id = user_perm.user_id WHERE perm.value = ?1", nativeQuery = true)
    List<User> findAllByRole(String role);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    Page<User> listUsers(Pageable pageable);

}
