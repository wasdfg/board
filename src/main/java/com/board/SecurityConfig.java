package com.board;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests(auth-> auth.requestMatchers("/**","/h2-console/**").permitAll());
        // authorizeHttpRequests() : security 처리에 HttpServletRequest를 이용한다는 것을 의미
        // requestMatchers : 특정 리소스에 대해서 권한을 설정
        //permitAll : requestMatchers로 설정한 리소스의 접근을 인증절차 없이 허용
        return http.build();
    }
}
