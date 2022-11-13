package com.mediasoft.services.demo.services;

import com.mediasoft.services.demo.dto.JwtUser;
import com.mediasoft.services.demo.entities.User;
import com.mediasoft.services.demo.repositories.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private IUser repo;
    @Override
    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Login user invalid!"));
        return new JwtUser(appUser);
    }
}
