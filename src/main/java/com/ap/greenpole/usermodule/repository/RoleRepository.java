package com.ap.greenpole.usermodule.repository;

import com.ap.greenpole.usermodule.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 18-Aug-20 04:51 AM
 */
public interface RoleRepository extends JpaRepository<Role, Long>, PagingAndSortingRepository<Role, Long> {

    @Query(value = "SELECT * FROM roles WHERE value = ?1", nativeQuery = true)
    Optional<Role> findByValue(String value);

}
