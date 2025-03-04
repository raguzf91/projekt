package hr.fina.student.projekt.entity;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails, OidcUser {
    
    private final User user;
    private final Role role;
    private final OidcIdToken oidcIdToken;
    private final OidcUserInfo oidcUserInfo;

    public UserPrincipal(User user, Role role) {
        this.user = user;
        this.role = role;
        this.oidcIdToken = null;
        this.oidcUserInfo = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(this.role.getPermission().split(",".trim())).map(SimpleGrantedAuthority::new).collect(toList());
    }
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }
    @Override
    public String getUsername() {
        return this.user.getEmail();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.user.isAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEnabled();
    }

    public User getUser() {
        return this.user;
    }

    public Role getRole() {
        return this.role;
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getAttributes() {

        return (this.oidcUserInfo == null ? Collections.emptyMap()  : this.oidcUserInfo.getClaims());
    }

    @Override
    public String getName() {
        return this.user.getFirstName();
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getClaims() {
        return (oidcUserInfo == null) ? Collections.emptyMap() : oidcUserInfo.getClaims();
    }

    @JsonIgnore
    @Override
    public OidcUserInfo getUserInfo() {
        return this.oidcUserInfo;
    }

    @JsonIgnore
    @Override
    public OidcIdToken getIdToken() {
        return this.oidcIdToken;
    }


}
