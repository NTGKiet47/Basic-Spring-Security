package com.example.Combine.Security.Methods.securityConfig;

import com.example.Combine.Security.Methods.entity.Account;
import com.example.Combine.Security.Methods.entity.Role;
import com.example.Combine.Security.Methods.repository.AccountRepository;
import com.example.Combine.Security.Methods.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        return processOAuth2User(oauth2User);
    }

    public OAuth2User processOAuth2User(OAuth2User oauth2User) {
        try {
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");

            Account account = accountRepository.findByEmail(email);

            if (account == null) {
                var username = email.substring(0, email.indexOf('@'));
                account = Account.builder().fullname(name).username(username)
                        .password(passwordEncoder.encode("defaultPassword")).email(email).build();

                Role defaultRole = roleRepository.findByRoleName("USER");
                if (defaultRole == null) {
                    defaultRole = new Role();
                    defaultRole.setRoleName("USER");
                    defaultRole = roleRepository.save(defaultRole);
                }
                List<Role> roles = new ArrayList<>();
                roles.add(defaultRole);
                account.setRoles(roles);

                account = accountRepository.save(account);
            }

            List<GrantedAuthority> authorities = account.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                    .collect(Collectors.toList());

            return new DefaultOAuth2User(
                    authorities,
                    oauth2User.getAttributes(), "email");
        } catch (Exception e) {
            log.error("Error while processing OAuth2 User", e.getMessage(), e);
            throw new RuntimeException("Error while processing OAuth2 User");
        }
    }
}
