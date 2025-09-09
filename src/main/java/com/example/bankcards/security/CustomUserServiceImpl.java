package com.example.bankcards.security;


import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserDetailsService} that loads user details
 * from the database using {@link UserRepository}.
 */
@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements UserDetailsService {

    /** Repository for accessing user data. */
    private final UserRepository userRepository;

    /**
     * Loads the user by their username (email in this case).
     *
     * @param username the username (email) of the user to load
     * @return {@link CustomUserDetails} containing user
     * information and authorities
     * @throws UsernameNotFoundException if the user
     * with the given username does not exist
     */
    @Override
    public CustomUserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
