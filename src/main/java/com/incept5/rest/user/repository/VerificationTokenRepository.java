package com.incept5.rest.user.repository;

import com.incept5.rest.user.domain.VerificationToken;
import com.incept5.rest.user.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 14/09/2012
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Query("select t from VerificationToken t where uuid = ?")
    VerificationToken findByUuid(String uuid);

    @Query("select t from VerificationToken t where token = ?")
    VerificationToken findByToken(String token);
}
