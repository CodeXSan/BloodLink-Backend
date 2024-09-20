package com.project.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.model.Admin;
import com.project.model.Donor;
import com.project.model.Role;
import com.project.repository.AdminRepository;
import com.project.repository.DonorRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Donor donor = donorRepository.findByEmail(identifier)
                .orElse(donorRepository.findByPhoneNumber(identifier)
                        .orElse(null));

        if (donor != null) {
            Role donorRole = donor.getRole();

            Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(donorRole.getName().name()));

            return new UserDetailsImpl(donor.getId(), donor.getEmail(), donor.getPhoneNumber(), donor.getPassword(), authorities);
        }

        Admin admin = adminRepository.findByEmail(identifier)
                .orElse(adminRepository.findByPhoneNumber(identifier)
                        .orElse(null));

        if (admin != null) {
            Role adminRole = admin.getRole();

            Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(adminRole.getName().name()));

            return new UserDetailsImpl(admin.getId(), admin.getEmail(), admin.getPhoneNumber(), admin.getPassword(), authorities);
        }

        // If neither a donor nor an admin is found, throw an exception
        throw new UsernameNotFoundException("User not found with identifier: " + identifier);
    }
  }
