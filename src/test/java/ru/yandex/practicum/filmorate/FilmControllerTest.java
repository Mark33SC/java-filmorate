package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    MockMvc mockMvc;
    Film film;
    @Autowired
    ObjectMapper mapper;


    @Test
    void createValidUserResponseShouldBeOk() throws Exception {
        film = Film.builder().id(1).name("Brokeback Mountain").description("Very intresting film!").
                releaseDate(LocalDate.of(2000, 1, 1)).duration(60).build();
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void nameShouldNotBeEmpty() throws Exception {
        film = Film.builder().id(1).name("").description("Very intresting film!")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(60).build();
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void descriptionExceedsMaxSymbols() throws Exception {
        film = Film.builder().id(1).name("Brokeback Mountain")
                .description("Very intresting film!Very intresting film!Very intresting film!Very intresting film!" +
                        "Very intresting film!Very intresting film!Very intresting film!Very intresting film!" +
                        "Very intresting film!Very intresting film!Very intresting film!Very intresting film!Very " +
                        "intresting film!Very intresting film!Very intresting film!")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(60).build();
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void descriptionExceedsMinSymbols() throws Exception {
        film = Film.builder().id(1).name("Brokeback Mountain").description("")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(60).build();
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotBeEarlierTheEarliestDate() throws Exception {
        film = Film.builder().id(1).name("Brokeback Mountain").description("Very intresting film!")
                .releaseDate(LocalDate.of(1000, 1, 1)).duration(60).build();
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void durationShouldBePositive() throws Exception {
        film = Film.builder().id(1).name("Brokeback Mountain").description("Very intresting film!")
                .releaseDate(LocalDate.of(-1000, 1, 1)).duration(60).build();
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
