package com.sl.shortlink.repo;

import com.sl.shortlink.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlShortenerRepo extends JpaRepository<UrlMapping, Long> {

    boolean existsByShortCode(String shortCode);

    Optional<UrlMapping> findByShortCode (String shortCode);

    List<UrlMapping> findByAppUser_Username(String username);

}
