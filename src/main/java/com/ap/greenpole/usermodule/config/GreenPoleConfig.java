package com.ap.greenpole.usermodule.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.thecarisma.InvalidEntryException;
import io.github.thecarisma.Konfiger;
import io.github.thecarisma.KonfigerStream;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 20-Aug-20 02:11 AM
 */
@Configuration
@EnableWebMvc
public class GreenPoleConfig implements WebMvcConfigurer {

    public static Konfiger userManagementProperties;

    // I need to load manually because other module depends on this and they might not have the
    // redis variables set, so i need to load it from user management own application.properties,
    // also preload path to ignore into the orRequestMatcher object
    static {
        try {
            InputStream stream = JwtRequestFilter.class.getResourceAsStream("/usermodule.properties");
            Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
            KonfigerStream kStream = new KonfigerStream(scanner.hasNext() ? scanner.next() : "");
            kStream.setCommentPrefix("#");
            userManagementProperties = new Konfiger(kStream);
        } catch (IOException | InvalidEntryException e) {
            e.printStackTrace();
        }
    }

    public static String resolveOptionalEnvFromSystem(String unprocessed) {
        if (unprocessed == null) {
            return unprocessed;
        }
        String processed = unprocessed;
        if (unprocessed.trim().startsWith("$")) {
            unprocessed = unprocessed.replace("${", "").replace("}", "");
            String[] arr = unprocessed.trim().split(":");
            processed = System.getenv().getOrDefault(arr[0], arr[1]);
        }
        return processed;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GreenPoleMethodInterceptor());
    }

}
