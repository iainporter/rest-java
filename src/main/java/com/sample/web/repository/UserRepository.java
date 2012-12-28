package com.sample.web.repository;

import com.sample.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 12/04/2012
 */
public interface UserRepository  extends JpaRepository<User, Long> {

    User findByEmailAddress(String emailAddress);

    @Query("select u from User u where uuid = ?")
    User findByUuid(String uuid);


}
