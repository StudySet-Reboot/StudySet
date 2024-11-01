package com.studyset.config;

import com.studyset.service.OAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final OAuthService oAuthService;

    public SecurityConfig(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // HTTP 기본 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())
                // 기본 로그인 X
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login") // 사용자 정의 로그인 페이지 설정
                                .permitAll() // 로그인 페이지에 모든 사용자가 접근 가능
                                .defaultSuccessUrl("/users/main") // 로그인 성공 후 리디렉션 URL 설정
                                .failureUrl("/login?error") // 로그인 실패 시 리디렉션 URL 설정
                )
                // 상태 비저장 세션 관리 정책 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // 요청 인가 처리 설정
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login").permitAll() // 로그인 페이지는 공용으로 설정
                                .requestMatchers("/", "/h2-console/**", "/style/**", "/js/**", "/images/**").permitAll() // 정적 자원에 대한 접근 허용
                                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/login") // 사용자 정의 로그인 페이지 설정
                                .successHandler(new MyAuthenticationSuccessHandler())
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint.userService(oAuthService)
                                )
                )
                // 로그아웃 설정
                .logout(logout ->
                        logout
                                .logoutUrl("/logout") // 로그아웃 요청 URL
                                .logoutSuccessUrl("/login") // 로그아웃 성공 후 리디렉션 URL
                                .invalidateHttpSession(true) // 세션 무효화
                                .deleteCookies("JSESSIONID") // 쿠키 삭제
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("https://studyset.shop");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
