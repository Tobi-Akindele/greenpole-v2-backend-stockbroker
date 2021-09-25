package com.ap.greenpole.usermodule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 04-Jun-20 12:07 AM
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public static OrRequestMatcher orRequestMatcher;

    static String[] openUrls = new String[] {
            "/user/register_admin",
            "/user/send_password_reset_email",
            "/user/reset_password",
            "/user/login",
            "/actuator",
            "/actuator/**",
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    @Override
    public void configure(WebSecurity web) throws Exception {
        List<RequestMatcher> openUrlMatchers = new ArrayList<>();
        for (String openUrl : openUrls) {
            openUrlMatchers.add(new AntPathRequestMatcher(openUrl));
        }
        String[] openUrls2 = getOpenUrlsEnv().split(",");
        for (String openUrl : openUrls2) {
            if (openUrl != null && !openUrl.isEmpty()) {
                openUrlMatchers.add(new AntPathRequestMatcher(openUrl));
            }
        }
        orRequestMatcher = new OrRequestMatcher(openUrlMatchers);
        web.ignoring().requestMatchers(orRequestMatcher);
    }

    public String getOpenUrlsEnv() {
        String openUrl = env.getProperty("app.open-urls");
        if (openUrl == null && GreenPoleConfig.userManagementProperties != null) {
            openUrl = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("app.open-urls", ""));
        }
        return openUrl;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests().antMatchers(openUrls).permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        //httpSecurity.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
    }
}
