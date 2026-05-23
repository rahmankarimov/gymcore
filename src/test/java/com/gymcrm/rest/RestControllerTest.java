package com.gymcrm.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.config.AppConfig;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.rest.dto.AddTrainingRequest;
import com.gymcrm.rest.dto.TraineeRegistrationRequest;
import com.gymcrm.rest.dto.TrainerRegistrationRequest;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import com.gymcrm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

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
    private InMemoryStorage storage;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        storage.clear();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new AuthController(traineeService, trainerService),
                        new TraineeController(traineeService),
                        new TrainerController(trainerService),
                        new TrainingController(trainingService))
                .setControllerAdvice(new RestExceptionHandler())
                .addFilters(new RestLoggingFilter())
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
        mockMvc.perform(get("/api/login")
                        .param("username", "Ali.Valiyev")
                        .param("password", password))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectProtectedEndpointWithoutBasicAuth() throws Exception {
        mockMvc.perform(get("/api/trainees/profile").param("username", "Ali.Valiyev"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Basic authentication is required"));
    }

    @Test
    void shouldReturnAssignedTrainersAndTrainings() throws Exception {
        Trainee trainee = traineeService.createProfile(new Trainee(null, "Sara", "Mammadova", null, null,
                true, LocalDate.of(1998, 3, 4), "Baku"));
        Trainer trainer = trainerService.createProfile(new Trainer(null, "Orkhan", "Karimov", null, null,
                true, "Fitness"));

        mockMvc.perform(post("/api/trainings")
                        .header("Authorization", basic(trainee.getUsername(), trainee.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddTrainingRequest(
                                trainee.getUsername(), trainer.getUsername(), "Morning Training",
                                LocalDate.of(2026, 5, 1), 60))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/trainees/trainings")
                        .header("Authorization", basic(trainee.getUsername(), trainee.getPassword()))
                        .param("username", trainee.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trainingName").value("Morning Training"))
                .andExpect(jsonPath("$[0].trainingType").value("Fitness"))
                .andExpect(jsonPath("$[0].trainerName").value("Orkhan Karimov"));
    }

    @Test
    void shouldRegisterTrainerAndReturnTrainingTypes() throws Exception {
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TrainerRegistrationRequest("Leyla", "Aliyeva", "Yoga"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Leyla.Aliyeva"));

        mockMvc.perform(get("/api/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingTypes").isArray());
    }

    private String basic(String username, String password) {
        String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }
}
