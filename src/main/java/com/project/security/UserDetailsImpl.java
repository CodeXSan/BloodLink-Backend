package com.project.security;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.model.Admin;
import com.project.model.Donor;
import com.project.model.Role;
import com.project.repository.RoleRepository;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private String id;

	private String identifier;

	private String email;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(String id, String identifier, String email, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.identifier = identifier;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(Donor donor, RoleRepository roleRepository) {
		Role donorRole = donor.getRole();

        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(donorRole.getName().toString()));


		return new UserDetailsImpl(
				donor.getId(), 
				donor.getPhoneNumber(), 
				donor.getEmail(), 
				donor.getPassword(), 
				authorities);
	}

	public static UserDetailsImpl build(Admin admin, RoleRepository roleRepository) {
		Role adminRole = admin.getRole();

        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(adminRole.getName().toString()));


		return new UserDetailsImpl(
				admin.getId(), 
				admin.getPhoneNumber(), 
				admin.getEmail(), 
				admin.getPassword(), 
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return identifier;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}