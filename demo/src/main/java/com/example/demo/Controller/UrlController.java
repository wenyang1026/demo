package com.example.demo.Controller;

import com.example.demo.Service.UrlShorteningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/urldata")
public class UrlController {
    @Autowired
    private UrlShorteningService shorteningService;

    @PostMapping("/shortner")
    public String shortenUrl(@RequestParam String longUrl) throws IOException {
        String shortUrl = shorteningService.shortenUrl(longUrl);
        System.out.println("short url =" + shortUrl);
        System.out.println("long url =" + longUrl);
        return shortUrl;
    }

    @GetMapping("/fetch/{shortUrl}")
    public ResponseEntity<String> fetchLongUrl(@PathVariable String shortUrl) {
        System.out.println("Attempting to fetch long URL for short URL: " + shortUrl);
        String longUrl = shorteningService.getOriginalUrl(shortUrl);

        if (longUrl != null) {
            System.out.println("Long URL is: " + longUrl);
            return ResponseEntity.ok().body(longUrl);
        } else {
            System.out.println("No long URL found for the given short URL: " + shortUrl);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "names", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }


}
