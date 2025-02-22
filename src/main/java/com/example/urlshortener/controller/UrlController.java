package com.example.urlshortener.controller;

import com.example.urlshortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/url")
public class UrlController {

    @Autowired
    private UrlService urlService;

    // API to shorten a URL
    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String originalUrl) {
        originalUrl = originalUrl.trim().replaceAll("^\"|\"$", "");

        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            originalUrl = "https://" + originalUrl;
        }
        String shortUrl = urlService.shortenUrl(originalUrl);
        return ResponseEntity.ok(shortUrl);
    }

    // API to redirect to the original URL
    @GetMapping("/redirect/{shortUrl}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortUrl) {
        // Trim any unexpected characters (optional safety check)
        shortUrl = shortUrl.trim().replaceAll("^\"|\"$", "");

        System.out.println("Received request to redirect short URL: " + shortUrl);

        String originalUrl = urlService.getOriginalUrl(shortUrl);

        if (originalUrl != null) {
            System.out.println("Redirecting to: " + originalUrl);

            // Increment click count
            urlService.incrementClickCount(shortUrl);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", originalUrl)
                    .build();
        } else {
            System.out.println("Short URL not found: " + shortUrl);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL not found");
        }
    }

}

