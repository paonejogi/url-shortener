package com.example.urlshortener.service;

import com.example.urlshortener.model.Url;
import com.example.urlshortener.repository.UrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    // Method to shorten URL
    public String shortenUrl(String originalUrl) {
        // Encode URL to generate a short hash
//        String encodedUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(originalUrl.getBytes(StandardCharsets.UTF_8));
        String shortUrl = UUID.randomUUID().toString().substring(0, 8);

        // Store in DB
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        url.setCreatedAt(LocalDateTime.now());
        url.setClickCount(0);

        urlRepository.save(url);
        System.out.println("Saved Short URL: " + shortUrl);
        return shortUrl;
    }

    // Method to retrieve original URL
    public String getOriginalUrl(String shortUrl) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);
        return urlOptional.map(Url::getOriginalUrl).orElse(null);
    }

    //Increment click count when URL is accessed
    @Transactional
    public void incrementClickCount(String shortUrl) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);
        urlOptional.ifPresent(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
        });
    }
}
