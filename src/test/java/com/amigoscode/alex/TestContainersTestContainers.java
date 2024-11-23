package com.amigoscode.alex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestContainersTestContainers extends AbstractTestContainers {

    @Test
    void canStartPostgresDB() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

}
