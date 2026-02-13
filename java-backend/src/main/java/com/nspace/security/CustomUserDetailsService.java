package com.nspace.security;

import com.nspace.model.User;
import com.nspace.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                // SECURITY CRITICAL: Mapping der DB-Rolle (z.B. "ROLE_ADMIN") zur Spring
                // Authority.
                // Ohne das weiß Spring Security nicht, dass dieser User Admin-Rechte hat!
                java.util.List
                        .of(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole())));
    }
}
