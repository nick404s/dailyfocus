package com.nick404s.dailyfocus.repository;

import com.nick404s.dailyfocus.model.User;
import com.nick404s.dailyfocus.util.AppRoles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> { // Long for id
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // get the number of all admins in the db. Query all User entities. u - is an alias for any user, a - for the authorities
    @Query("SELECT COUNT(u) FROM User u JOIN u.authorities a WHERE a.authority = '" + AppRoles.ROLE_ADMIN + "'")
    long countAdminUsers();
}
