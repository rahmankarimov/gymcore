package com.gymcrm.security;

import com.gymcrm.dao.UserDao;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GymUserDetailsService implements UserDetailsService {
    private final UserDao userDao;

    public GymUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isActive())
                .authorities(authorities(user))
                .build();
    }

    private List<SimpleGrantedAuthority> authorities(User user) {
        if (user instanceof Trainee) {
            return List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"));
        }
        if (user instanceof Trainer) {
            return List.of(new SimpleGrantedAuthority("ROLE_TRAINER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
