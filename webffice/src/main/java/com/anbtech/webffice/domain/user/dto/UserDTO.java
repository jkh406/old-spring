package com.anbtech.webffice.domain.user.dto;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements UserDetails {
    private String user_ID;
    private String user_name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String user_PW;
    private String user_role;
    private String create_Date;
    private String update_Date;
    private String avatar;
    private String authority;
    private String page_url;
    private String req_method;
    
    public Map<String, Object> getUserDataMap() {
        Map<String, Object> userDataMap = new LinkedHashMap<>();
        userDataMap.put("user_ID", this.user_ID);
        userDataMap.put("user_name", this.user_name);
        userDataMap.put("user_role", this.user_role);
        userDataMap.put("avatar", this.avatar);
        return userDataMap;
    }
    
    public Map<String, Object> getPageAuthority() {
        Map<String, Object> pageAuthorityMap = new LinkedHashMap<>();
        pageAuthorityMap.put("user_ID", this.user_ID);
        pageAuthorityMap.put("authority", this.authority);
        pageAuthorityMap.put("page_url", this.page_url);
        return pageAuthorityMap;
    }

    // 이하 코드는 security 를 위한 용도
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.user_PW;
    }

    @Override
    public String getUsername() {
        return this.user_name;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}