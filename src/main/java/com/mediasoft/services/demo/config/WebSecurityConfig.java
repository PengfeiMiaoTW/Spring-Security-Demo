package com.mediasoft.services.demo.config;

import com.mediasoft.services.demo.filter.JWTAccessDeniedHandler;
import com.mediasoft.services.demo.filter.JWTAuthenticationEntryPoint;
import com.mediasoft.services.demo.filter.JWTAuthenticationFilter;
import com.mediasoft.services.demo.filter.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    String[] resources = new String[]{
            "/include/**",
            "/css/**",
            "/icons/**",
            "/img/**",
            "/js/**",
            "/layer/**"
    };


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);
        http
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(resources)
                .permitAll()
                .antMatchers("/login", "/index","/signup")
                .permitAll()
                .anyRequest()
                .permitAll()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                .accessDeniedHandler(new JWTAccessDeniedHandler())
                .and()
//                .formLogin(Customizer.withDefaults());
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("https://www.baidu.com")
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll();
//                .and()
//                .logout()
//                .permitAll()
//                .logoutSuccessUrl("/login?logout");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
