package com.poweroftwo.potms_backend.test_containers;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class TestContainers {
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("potms")
                    .withUsername("postgres")
                    .withPassword("1234");

    protected static final GenericContainer<?> REDIS = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379)
            .withCommand("localhost", "--requirepass", "");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = POSTGRES_CONTAINER.getJdbcUrl() + "?sslmode=disable";
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.redis.ssl.enabled", () -> "false");
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    public String getJdbcLink(){
        return POSTGRES_CONTAINER.getJdbcUrl();
    }

    public String getUserName(){
        return POSTGRES_CONTAINER.getUsername();
    }

    public String getPassword(){
        return POSTGRES_CONTAINER.getPassword();
    }

    @BeforeAll
    public static void startContainer() {
        if (!POSTGRES_CONTAINER.isRunning()) {
            POSTGRES_CONTAINER.start();
        }
    }
}