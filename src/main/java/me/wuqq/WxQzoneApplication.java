package me.wuqq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import me.wuqq.core.Fetcher;
import me.wuqq.core.Fetcher.InvalidCredentialException;
import me.wuqq.domain.Credential;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootApplication
@Configuration
public class WxQzoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(WxQzoneApplication.class, args);
    }

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Component
    public static final class Startup implements CommandLineRunner {
        @Autowired
        Fetcher mFetcher;

        @Override
        public void run(final String... args) throws Exception {
            try {
                val credential = this.parseArgument(args);

                mFetcher.fetch(credential);

                System.out.println("\n\nFetching done!");
            } catch (InvalidCredentialException e) {
                System.out.println("Illegal cookie file provided.");
            } catch (Exception e) {
                System.out.println(e.getMessage());

                this.dumpError(e);
            }
        }

        @SneakyThrows
        private void dumpError(final Exception e) {
            val debugFile = "wx-qzone-debug.log";

            try (val writer = new FileWriter(debugFile);
                 val printer = new PrintWriter(writer)) {
                e.printStackTrace(printer);
            }

            System.out.println("\nError details are dumped into " + debugFile);
        }

        private Credential parseArgument(final String... args) {
            if (args.length != 1) {
                this.bark(args);
            }

            val cookieFile = args[0];

            try {
                val cookie = new String(Files.readAllBytes(Paths.get(cookieFile)), UTF_8);

                return Credential.fromCookie(cookie);
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot load cookie file: " + cookieFile);
            }
        }

        private void bark(final String[] args) {
            System.out.printf("Cookie file not specified");
            System.exit(1);
        }
    }
}
