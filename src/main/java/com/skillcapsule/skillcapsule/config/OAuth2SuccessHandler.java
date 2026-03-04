package com.skillcapsule.skillcapsule.config;

import com.skillcapsule.skillcapsule.entity.User;
import com.skillcapsule.skillcapsule.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {

            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider("GOOGLE");

            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(email);

        String redirectUrl =
                "https://skillcapsule-frontend-17j1.vercel.app/dashboard"
                        + "?token=" + token
                        + "&id=" + user.getId()
                        + "&name=" + user.getName()
                        + "&email=" + user.getEmail();

        response.sendRedirect(redirectUrl);
    }
}