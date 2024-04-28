package com.example.demo.Repository;

import com.example.demo.Entity.UrlData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlDataRepository extends JpaRepository<UrlData, Long> {

    Optional<UrlData> findByShortUrl(String shortUrl);
}
