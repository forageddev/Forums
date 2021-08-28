package gg.manny.forums;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.manny.forums.rank.Rank;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.web.controller.StaffController;
import lombok.Getter;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Random;

@Getter
@SpringBootApplication
public class Application {

    public static final Random RANDOM = new Random();

    public static final Parser MARKDOWN_PARSER = Parser.builder().build();
    public static final HtmlRenderer MARKDOWN_RENDERER = HtmlRenderer.builder().build();

    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    @Getter private static Application instance;

    public Application() {
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        instance = new Application();
    }

    public static void debug(String title, String... messages) {
        System.out.println(" - - - - - - - DEBUGGING: " + title + " - - - - - - - - - ");
        for (String message : messages) {
            System.out.println(" - -> " + message);
        }
        System.out.println(" - - - - - - - - - END OF DEBUG - - - - - - - - - - - ");
    }

    @Bean
    CommandLineRunner init(RankRepository roleRepository) {

        return args -> {
            if (!roleRepository.findById("default").isPresent()) {
                Rank role = new Rank();
                role.setId("default");
                role.setName("Default");
                role.setColor("&f");
                role.setForumColor("#ffff");
                role.setWeight(0);
                roleRepository.save(role);
            }
        };

    }
}
