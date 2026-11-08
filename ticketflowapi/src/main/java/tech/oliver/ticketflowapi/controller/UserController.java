package tech.oliver.ticketflowapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/me")
    public ResponseEntity <Map<String, Object>> me(Jwt jwt) {

        return ResponseEntity.ok(jwt.getClaims());
        
    }
}
