package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
@ComponentScan(basePackages = {"ru.practicum.shareit.*"})
@EntityScan("ru.practicum.shareit.*")
@EnableJpaRepositories("ru.practicum.shareit.*")
class ShareItServerTests {

    @Test
    void contextLoads() {
    }

}
