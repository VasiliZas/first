package io.vasilizas.config;

import io.vasilizas.bean.db.User;
import io.vasilizas.repositories.jpa.UserRepository;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN = "/login";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .antMatcher("/**")
//                .authorizeRequests()
//                .antMatchers(LOGIN, "/error").permitAll()
//                .anyRequest().authenticated()
//                .and().formLogin().loginPage(LOGIN).failureForwardUrl(LOGIN)
//                .and().oauth2Login()
//                .defaultSuccessUrl("/home")
//                .and().logout().logoutSuccessUrl("/").permitAll();
        http.antMatcher("/**")
        .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login().defaultSuccessUrl("/home")
                .and().logout().logoutSuccessUrl("/").permitAll()
                .and().csrf().disable();
    }

    @Bean
    public PrincipalExtractor principalExtractor(UserRepository userRepository) {
        return map -> {
            String id = (String) map.get("sub");

            User user = userRepository.findById(id).orElseGet(() -> {
                User newUser = new User();
                newUser.setId(id);
                newUser.setName((String) map.get("name"));
                newUser.setEmail((String) map.get("email"));
                newUser.setGender((String) map.get("gender"));
                newUser.setLocale((String) map.get("locale"));
                newUser.setUserpic((String) map.get("picture"));
                return newUser;
            });
            user.setLastVisit(LocalDateTime.now());
            return userRepository.save(user);
        };
    }
}