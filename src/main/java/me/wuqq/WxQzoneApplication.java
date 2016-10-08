package me.wuqq;

import lombok.SneakyThrows;
import lombok.val;
import me.wuqq.core.BadCredentialException;
import me.wuqq.core.Driver;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileWriter;
import java.io.PrintWriter;

@SpringBootApplication
public class WxQzoneApplication {

	public static void main(String[] args) {
        try {
            val context = SpringApplication.run(WxQzoneApplication.class, args);
            val driver = context.getBean(Driver.class);

            driver.fetch();

            System.out.println("\n\nFetching done!");
        } catch (BadCredentialException e) {
            System.out.println(e.getMessage());
        } catch (BeanCreationException e) {
            System.out.println(e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());

            dumpError(e);
        }
    }

    @SneakyThrows
    private static void dumpError(final Exception e) {
        val debugFile = "wx-qzone-debug.log";

        try (val writer = new FileWriter(debugFile);
             val printer = new PrintWriter(writer)) {
            e.printStackTrace(printer);
        }

        System.out.println("\nSee error details in " + debugFile);
    }
}
