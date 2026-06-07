package com.gymcrm.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.TestDatabaseCleaner;
import com.gymcrm.config.AppConfig;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.rest.dto.AddTrainingRequest;
import com.gymcrm.rest.dto.AuthCredentials;
import com.gymcrm.rest.dto.TraineeRegistrationRequest;
import com.gymcrm.rest.dto.TrainerRegistrationRequest;
import com.gymcrm.security.BruteForceProtectionService;
import com.gymcrm.security.JwtAuthenticationFilter;
import com.gymcrm.security.JwtBlacklistService;
import com.gymcrm.security.JwtService;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(AppConfig.class)
class RestControllerTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    @Autowired
    private BruteForceProtectionService bruteForceProtectionService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        databaseCleaner.clean();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new AuthController(traineeService, trainerService, authenticationManager, jwtService,
                                jwtBlacklistService, bruteForceProtectionService),
                        new TraineeController(traineeService),
                        new TrainerController(trainerService),
                        new TrainingController(trainingService))
                .setControllerAdvice(new RestExceptionHandler())
                .addFilters(jwtAuthenticationFilter, new RestLoggingFilter(new SimpleMeterRegistry()))
                .build();
    }

    @Test
    void shouldRegisterTraineeAndLogin() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(
                "Ali", "Valiyev", LocalDate.of(2000, 1, 1), "Baku");

        String response = mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ali.Valiyev"))
                .andExpect(jsonPath("$.password").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String password = objectMapper.readTree(response).get("password").asText();
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthCredentials("Ali.Valiyev", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void shouldRejectProtectedEndpointWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/trainees/profile").param("username", "Ali.Valiyev"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

    @Test
    void shouldReturnAssignedTrainersAndTrainings() throws Exception {
        Trainee trainee = traineeService.createProfile(new Trainee(null, "Sara", "Mammadova", null, null,
                true, LocalDate.of(1998, 3, 4), "Baku"));
        Trainer trainer = trainerService.createProfile(new Trainer(null, "Orkhan", "Karimov", null, null,
                true, "Fitness"));
        String authorization = bearer(trainee.getUsername());

        mockMvc.perform(post("/api/trainings")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddTrainingRequest(
                                trainee.getUsername(), trainer.getUsername(), "Morning Training",
                                LocalDate.of(2026, 5, 1), 60))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/trainees/trainings")
                        .header("Authorization", authorization)
                        .param("username", trainee.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trainingName").value("Morning Training"))
                .andExpect(jsonPath("$[0].trainingType").value("Fitness"))
                .andExpect(jsonPath("$[0].trainerName").value("Orkhan Karimov"));
    }

    @Test
    void shouldRegisterTrainerAndRequireAuthenticationForTrainingTypes() throws Exception {
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TrainerRegistrationRequest("Leyla", "Aliyeva", "Yoga"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Leyla.Aliyeva"));

        mockMvc.perform(get("/api/trainings/types"))
                .andExpect(status().isUnauthorized());
    }

    private String bearer(String username) {
        return "Bearer " + jwtService.generateToken(username);
    }
}
