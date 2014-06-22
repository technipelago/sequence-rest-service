package se.technipelago.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by goran on 2014-06-22.
 */
@Configuration
@EnableWebSecurity
public class SequenceNumberSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${user.password}")
    private String userPassword;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // enable in memory based authentication with a user named
                // "user" and "admin"
                .inMemoryAuthentication()
                .withUser("user").password(userPassword).roles("USER").and()
                .withUser("admin").password(adminPassword).roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }
}