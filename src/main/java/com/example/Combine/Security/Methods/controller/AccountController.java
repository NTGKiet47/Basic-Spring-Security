/*******************************************************************************
 * Class        ：AccountController
 * Created date ：2025/02/26
 * Lasted date  ：2025/02/26
 * Author       ：KietNTG
 * Change log   ：2025/02/26：01-00 KietNTG create a new
 ******************************************************************************/
package com.example.Combine.Security.Methods.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.Combine.Security.Methods.entity.Account;
import com.example.Combine.Security.Methods.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * AccountController
 *
 * @author KietNTG
 * @version 01-00
 * @since 01-00
 */
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/home")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("home");
    }

    @GetMapping("/profile")
    public ResponseEntity<Account> profile() {
        try {
            return ResponseEntity.ok(accountService.profile());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(String username, String password) {
        return ResponseEntity.ok(accountService.register(username, password));
    }

}
