package me.wuqq;

import lombok.SneakyThrows;
import lombok.val;
import me.wuqq.core.Fetcher;
import me.wuqq.util.BadCredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootApplication
public class WxQzoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(WxQzoneApplication.class, args);
    }

    @Component
    public static final class Startup implements CommandLineRunner {
        @Autowired Fetcher mFetcher;

        @Override
        public void run(final String... args) throws Exception {
            try {
                val credential = this.getCookie(args);

                mFetcher.fetch(credential);

                System.out.println("\n\nFetching done!");
            } catch (IllegalArgumentException | BadCredentialException e) {
                System.out.println(e);
            } catch (Exception e) {
                System.out.println(e.getMessage());

                this.dumpError(e);
            }
        }

        private String getCookie(final String... args) {
            if (args.length != 1) {
                throw new IllegalArgumentException("Cookie file is not specific in args");
            }

            val cookieFile = args[0];

            try {
                val cookie = new String(Files.readAllBytes(Paths.get(cookieFile)), UTF_8);

                return cookie;
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot load cookie file: " + cookieFile);
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
    }
}
