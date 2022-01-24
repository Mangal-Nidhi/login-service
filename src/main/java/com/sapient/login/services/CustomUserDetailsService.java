package com.sapient.login.services;

import com.sapient.login.domain.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserProfile userProfile = userService.getUserProfile(userId);
        GrantedAuthority authority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "PS_USER";
            }
        };
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        return new User(userProfile.getEmailId(),
                "", authorities);
    }


}
