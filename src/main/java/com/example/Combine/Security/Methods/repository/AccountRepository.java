/*******************************************************************************
 * Class        ：AccountRepository
 * Created date ：2025/02/27
 * Lasted date  ：2025/02/27
 * Author       ：KietNTG
 * Change log   ：2025/02/27：01-00 KietNTG create a new
 ******************************************************************************/
package com.example.Combine.Security.Methods.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Combine.Security.Methods.entity.Account;

/**
 * AccountRepository
 *
 * @version 01-00
 * @since 01-00
 * @author KietNTG
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByUsername(String username);

    Account findByEmail(String email);


}
