package com.board;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.authenticator.SavedRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) //@PreAuthorize이 동작하게 기능 추가
public class SecurityConfig{
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests(auth-> auth.requestMatchers("/**","/h2-console/**","/user/findPwd/**").permitAll())
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**","/user/findPwd/**")))
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .formLogin((formLogin) -> formLogin
                        .loginPage("/user/login")
                        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler(){
                        })
                )
                        //.defaultSuccessUrl("/"))
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true))
        ;
        // authorizeHttpRequests() : security 처리에 HttpServletRequest를 이용한다는 것을 의미
        // requestMatchers : 특정 리소스에 대해서 권한을 설정
        //permitAll : requestMatchers로 설정한 리소스의 접근을 인증절차 없이 허용
        //csrf인증으로 h2-console에 접근 허용

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
