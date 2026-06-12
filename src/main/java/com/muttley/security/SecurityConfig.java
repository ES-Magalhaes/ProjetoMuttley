package com.muttley.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import java.util.function.Consumer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Injetamos o repositório que já tem as configurações do seu Google OAuth
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login/**", "/css/**", "/js/**", "/images/**", "/img/**", "/evento/inscrever/**").permitAll() 
                .anyRequest().authenticated()// Trava o resto
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                    // Chama o método aqui embaixo que adiciona o prompt=select_account
                    .authorizationRequestResolver(authorizationRequestResolver(this.clientRegistrationRepository)) 
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/") // Volta pra home ao sair
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    // Método que customiza a requisição pro Google
    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        
        authorizationRequestResolver.setAuthorizationRequestCustomizer(customizer());
        return authorizationRequestResolver;
    }

    // Aqui é onde a mágica acontece: obriga o Google a mostrar a tela de escolher conta
    private Consumer<OAuth2AuthorizationRequest.Builder> customizer() {
        return customizer -> customizer.additionalParameters(params -> params.put("prompt", "select_account"));
    }
}
