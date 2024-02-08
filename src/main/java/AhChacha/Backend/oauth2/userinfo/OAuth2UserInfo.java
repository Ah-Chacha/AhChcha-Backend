package AhChacha.Backend.oauth2.userinfo;

import AhChacha.Backend.domain.Member;
import AhChacha.Backend.domain.Platform;

import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();

    public abstract String getProfileImageUrl();

}
