package com.example.stocks.messaging;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@ExtendWith(MockitoExtension.class)
class SqsStockSeederTest {

    @Mock
    private SqsTemplate sqsTemplate;

    @Test
    void run_sendsAllSeedMessages() throws Exception {
        // Given
        SqsStockSeeder seeder = new SqsStockSeeder(sqsTemplate, "test-queue");

        // When
        seeder.run();

        // Then - sends 5 seed messages
        verify(sqsTemplate, times(5)).send(any());
    }

    @Test
    void constructor_throwsException_whenQueueNameIsNull() {
        // When/Then
        assertThatThrownBy(() -> new SqsStockSeeder(sqsTemplate, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("queueName must not be null");
    }
}
