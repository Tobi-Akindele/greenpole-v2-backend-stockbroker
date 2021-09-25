package com.ap.greenpole.usermodule.service;

import com.ap.greenpole.usermodule.model.EmailVerification;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.repository.EmailVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 07-Jun-20 06:52 PM
 */
@Service
public class EmailVerificationService {

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    public boolean hasTokenExpired(String token) {
        Optional<EmailVerification> emailVerification = getByToken(token);
        if (!emailVerification.isPresent()) {
            return false;
        }
        Date expiryDate = new Date(emailVerification.get().getExpiryDate() * 1000);

        if (expiryDate.before(new Date())) {
            changeVerificationStatus(emailVerification.get(), false);
            return true;
        }
        return  false;
    }

    public void changeVerificationStatus(EmailVerification emailVerification, boolean used) {
        emailVerification.setTokenUsed(used);
        emailVerificationRepository.save(emailVerification);
    }

    public int invalidatePendingEmailVerifications(long id, boolean used) {
        return emailVerificationRepository.invalidateEmailVerifications(id, used ? 1 : 0);
    }

    public Optional<EmailVerification> getByToken(String token) {
        return emailVerificationRepository.findByToken(token);
    }
}
