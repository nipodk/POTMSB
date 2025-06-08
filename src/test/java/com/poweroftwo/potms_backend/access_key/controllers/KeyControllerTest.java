package com.poweroftwo.potms_backend.access_key.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateRequest;
import com.poweroftwo.potms_backend.access_key.services.AccessKeyServiceImpl;
import com.poweroftwo.potms_backend.test_containers.TestContainers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest()
class KeyControllerTest extends TestContainers {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private AccessKeyServiceImpl accessKeyService;


    @Test
    void createKey() throws Exception {
        String newString = "Data: " + getJdbcLink() +
                getUserName() +
                getPassword();
        System.out.println(newString);
        try(Connection conn = DriverManager.getConnection(getJdbcLink(), getUserName(), getPassword())){
            final KeyCreateRequest keyCreateRequest = new KeyCreateRequest(
                    "keyName",
                    "apiKey",
                    "secreteKey",
                    new Date(),
                    "mail@gmail.com"
            );

            final String createRequestJson = objectMapper.writeValueAsString(keyCreateRequest);

            mockMvc.perform(
                            post("/api/v1/keys")
                                    .contentType("application/json")
                                    .content(createRequestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keyName").value("keyName"));
        }
        catch (Exception exception) {
            throw new Exception("Database connection error");
        }
    }

    @Test
    void getAllKeys() {
    }

    @Test
    void updateKey() {
    }

    @Test
    void deleteKey() {
    }
}