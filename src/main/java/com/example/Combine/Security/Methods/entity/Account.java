/*******************************************************************************
 * Class        ：Account
 * Created date ：2025/02/27
 * Lasted date  ：2025/02/27
 * Author       ：KietNTG
 * Change log   ：2025/02/27：01-00 KietNTG create a new
 ******************************************************************************/
package com.example.Combine.Security.Methods.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Account
 *
 * @author KietNTG
 * @version 01-00
 * @since 01-00
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer accountId;

    @Column(unique = true)
    String email;

    @Nullable
    String password;

    String fullname;

    String username;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    List<Role> roles;

}
