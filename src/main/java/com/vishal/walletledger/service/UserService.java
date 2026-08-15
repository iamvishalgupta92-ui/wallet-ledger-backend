package com.vishal.walletledger.service;

import com.vishal.walletledger.entity.User;
import com.vishal.walletledger.repository.UserRepository;
import com.vishal.walletledger.wallet.WalletService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            WalletService walletService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
    }

    @Transactional
    public User createUser(User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        User savedUser = userRepository.save(user);

        // Automatically create wallet for the new user
        walletService.createWallet(savedUser.getId());

        return savedUser;
    }
}