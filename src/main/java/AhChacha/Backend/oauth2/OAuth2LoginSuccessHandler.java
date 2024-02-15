package AhChacha.Backend.oauth2;

import AhChacha.Backend.dto.oauth.response.TokenResponse;
import AhChacha.Backend.domain.Platform;
import AhChacha.Backend.domain.RefreshToken;
import AhChacha.Backend.domain.RoleType;
import AhChacha.Backend.jwt.TokenProvider;
import AhChacha.Backend.repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {



    // 클라이언트에서 로그인 완료 이후 authentication 인증 이후 access token 까지 발급 받아야 함 !!!!!!!!!!

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login Success");
        System.out.println("success");
        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            System.out.println("authentication = " + authentication);
            System.out.println("customOAuth2User = " + customOAuth2User);
            System.out.println("customOAuth2User.getName() = " + customOAuth2User.getName());
            System.out.println("request = " + request);
            System.out.println("response = " + response);

            if(customOAuth2User.getRoleType() == RoleType.GUEST) {


                Platform platform = customOAuth2User.getPlatform();

                System.out.println("platform = " + platform);


                if (platform == Platform.GOOGLE) {
                    response.sendRedirect("/auth/sign-up/GOOGLE/"+authentication.getName());
                } else if (platform == Platform.KAKAO) {
                    response.sendRedirect("/auth/sign-up/KAKAO/"+authentication.getName());
                } else if (platform == Platform.NAVER) {
                    System.out.println("!!!!! = ");
                    Map<String, String> map = new HashMap<>();
                    String[] keyValuePairs = authentication.getName().substring(1, authentication.getName().length() - 1).split(", ");
                    for (String pair : keyValuePairs) {
                        String[] entry = pair.split("=");
                        map.put(entry[0], entry[1]);
                    }
                    String id = map.get("id");
                    System.out.println("id = " + id);
                    response.sendRedirect("/auth/sign-up/NAVER/"+id);
                }
                //response.sendRedirect("/auth/sign-up/"+ platform +"/"+authentication.getName());
                System.out.println("/auth/sign-up/"+ platform +"/"+authentication.getName());
                //회원가입 화면으로 redirect
                //refreshTokenRepository.save(refreshToken);
                //System.out.println("result = " + result);

            } else {
                TokenResponse tokenResponse = loginSuccess(response, customOAuth2User, authentication);
                System.out.println("tokenDto = " + tokenResponse);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");

                String result = objectMapper.writeValueAsString(tokenResponse);
                response.getWriter().write(result);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    //dto를 리턴하고 싶은데... redirect를 여기서 하려면.. response 함수를 써야하는듯..
    @Transactional
    public TokenResponse loginSuccess(HttpServletResponse response, CustomOAuth2User customOAuth2User, Authentication authentication) throws IOException {
        //refresh 토큰 확인?? or 로그인 시 마다 토큰 새로 발급?
        TokenResponse tokenResponse = tokenProvider.generateTokenDto(authentication);
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())   //refreshToken = platformId
                .value(tokenResponse.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenResponse;
    }




}




