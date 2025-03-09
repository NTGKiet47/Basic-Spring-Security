/*******************************************************************************
 * Class        ：AccountService
 * Created date ：2025/02/27
 * Lasted date  ：2025/02/27
 * Author       ：KietNTG
 * Change log   ：2025/02/27：01-00 KietNTG create a new
 ******************************************************************************/
package com.example.Combine.Security.Methods.service;

import javax.security.sasl.AuthenticationException;

import com.example.Combine.Security.Methods.entity.Role;
import com.example.Combine.Security.Methods.repository.RoleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Combine.Security.Methods.entity.Account;
import com.example.Combine.Security.Methods.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AccountService
 *
 * @author KietNTG
 * @version 01-00
 * @since 01-00
 */
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

public Account profile() throws AuthenticationException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        throw new AuthenticationException("Unauthorized");
    }

    var username = authentication.getName();
    var account = accountRepository.findByUsername(username);
    if (account == null) {
        throw new AuthenticationException("Unauthorized");
    }

    return account;
}

    public String register(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));

        Role defaultRole = roleRepository.findByRoleName("USER");
        if (defaultRole == null){
            defaultRole = new Role();
            defaultRole.setRoleName("USER");
            defaultRole = roleRepository.save(defaultRole);
        }

        account.setRoles(List.of(defaultRole));

        return accountRepository.save(account).getUsername();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);

        if (account == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        List<GrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());

        return User.withUsername(account.getUsername())
                .password(account.getPassword())
                .authorities(authorities)
                .build();
    }
}
