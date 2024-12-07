package encore.server.global.config;

import encore.server.global.auth.HttpCookieOAuth2AuthorizationRequestRepository;
import encore.server.global.auth.OAuth2SuccessHandler;
import encore.server.global.auth.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2UserService oAuth2UserService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll()
                )
                .httpBasic(Customizer.withDefaults())

                //OAUth 2.0
                .oauth2Login(oauth ->
                        // OAuth2 로그인 성공 이후 커스텀 클래스 실행
                        oauth
                                .userInfoEndpoint(custom -> custom.userService(oAuth2UserService))
                                .successHandler(oAuth2SuccessHandler)
                                //커스텀 state 저장 클래스
                                .authorizationEndpoint(custom ->
                                        custom.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                                )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
