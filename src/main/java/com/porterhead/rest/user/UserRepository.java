package com.porterhead.rest.user;

import com.porterhead.rest.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 12/04/2012
 */
public interface UserRepository  extends JpaRepository<User, Long> {

    User findByEmailAddress(String emailAddress);

    @Query("select u from User u where uuid = ?")
    User findByUuid(String uuid);

    @Query("select u from User u where u in (select user from AuthorizationToken where lastUpdated < ?)")
    List<User> findByExpiredSession(Date lastUpdated);

    @Query("select u from User u where u = (select user from AuthorizationToken where token = ?)")
    User findBySession(String token);

}
