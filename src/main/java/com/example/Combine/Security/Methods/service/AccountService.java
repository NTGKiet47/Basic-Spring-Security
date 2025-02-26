/*******************************************************************************
 * Class        ：AccountService
 * Created date ：2025/02/27
 * Lasted date  ：2025/02/27
 * Author       ：KietNTG
 * Change log   ：2025/02/27：01-00 KietNTG create a new
 ******************************************************************************/
package com.example.Combine.Security.Methods.service;

import javax.security.sasl.AuthenticationException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Combine.Security.Methods.entity.Account;
import com.example.Combine.Security.Methods.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

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
        account.setPassword(password);
        return accountRepository.save(account).getUsername();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);

        return User.withUsername(account.getUsername()).password(account.getPassword())
                .roles(account.getAccountType()).build();
    }
}
