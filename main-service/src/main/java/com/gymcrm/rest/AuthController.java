package com.gymcrm.rest;

import com.gymcrm.exception.ValidationException;
import com.gymcrm.rest.dto.AuthCredentials;
import com.gymcrm.rest.dto.LoginChangeRequest;
import com.gymcrm.rest.dto.LoginResponse;
import com.gymcrm.security.BruteForceProtectionService;
import com.gymcrm.security.JwtBlacklistService;
import com.gymcrm.security.JwtService;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Authentication")
@RestController
@RequestMapping("/api")
public class AuthController {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtBlacklistService jwtBlacklistService;
    private final BruteForceProtectionService bruteForceProtectionService;

    public AuthController(TraineeService traineeService, TrainerService trainerService,
                          AuthenticationManager authenticationManager, JwtService jwtService,
                          JwtBlacklistService jwtBlacklistService,
                          BruteForceProtectionService bruteForceProtectionService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtBlacklistService = jwtBlacklistService;
        this.bruteForceProtectionService = bruteForceProtectionService;
    }

    @ApiOperation("Login with username and password")
    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthCredentials credentials) {
        RestSupport.requireText(credentials.username(), "Username");
        RestSupport.requireText(credentials.password(), "Password");
        if (bruteForceProtectionService.isBlocked(credentials.username())) {
            throw new SecurityException("User is blocked for 5 minutes after 3 unsuccessful login attempts");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    credentials.username(), credentials.password()));
            bruteForceProtectionService.loginSucceeded(credentials.username());
            return new LoginResponse(jwtService.generateToken(credentials.username()), "Bearer");
        } catch (BadCredentialsException exception) {
            bruteForceProtectionService.loginFailed(credentials.username());
            throw new SecurityException("Invalid credentials");
        }
    }

    @ApiOperation("Logout current bearer token")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            jwtBlacklistService.blacklist(token, jwtService.extractExpiration(token));
        }
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Change user login password")
    @PutMapping("/login")
    public ResponseEntity<Void> changeLogin(@RequestBody LoginChangeRequest request) {
        RestSupport.requireText(request.username(), "Username");
        RestSupport.requireText(request.oldPassword(), "Old password");
        RestSupport.requireText(request.newPassword(), "New password");
        RestSupport.requireOwner(request.username(), RestSupport.authenticatedUsername());
        if (traineeService.authenticate(request.username(), request.oldPassword())) {
            traineeService.changePassword(request.username(), request.newPassword());
            return ResponseEntity.ok().build();
        }
        if (trainerService.authenticate(request.username(), request.oldPassword())) {
            trainerService.changePassword(request.username(), request.newPassword());
            return ResponseEntity.ok().build();
        }
        throw new SecurityException("Invalid credentials");
    }
}
