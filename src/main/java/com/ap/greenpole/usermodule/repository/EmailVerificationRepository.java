package com.ap.greenpole.usermodule.repository;

import com.ap.greenpole.usermodule.model.EmailVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 04-Jun-20 02:43 AM
 */
@Transactional
@Repository
public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Long> {

    @Query(value = "SELECT * FROM email_verifications WHERE user_id = ?1 AND email_address = ?2 AND token_used = 0", nativeQuery = true)
    List<EmailVerification> findByMember(long userId, String email, int operation, int status, Pageable pageable);

    @Query(value = "SELECT * FROM email_verifications WHERE token = ?1",
            nativeQuery = true)
    Optional<EmailVerification> findByToken(String token);

    @Modifying
    @Query(value = "UPDATE email_verifications SET token_used = 0 WHERE user_id = ?1 AND operation = ?2",
            nativeQuery = true)
    int invalidateEmailVerifications(long userId, int operation);

}
