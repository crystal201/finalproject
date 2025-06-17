package com.example.cinema.entity;

   import jakarta.persistence.*;
   import org.springframework.security.core.GrantedAuthority;
   import org.springframework.security.core.authority.SimpleGrantedAuthority;
   import org.springframework.security.core.userdetails.UserDetails;

   import java.util.Collection;
   import java.util.Collections;
   import java.util.List;

   @Entity
   @Table(name = "users")
   public class User implements UserDetails {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       @Column(name = "username", nullable = false, unique = true)
       private String username;

       @Column(name = "password", nullable = false)
       private String password;

       @Column(name = "email", nullable = false, unique = true)
       private String email;

       @Column(name = "phone")
       private String phone;

       @Column(name = "avatar")
       private String avatar;

       @Column(name = "enabled", nullable = false)
       private boolean enabled;
       @Column(name = "role")
       private String role;

       @Column(name = "is_verified", nullable = false)
       private boolean isVerified = false;

       // Getters and setters
       public Long getId() {
           return id;
       }

       public void setId(Long id) {
           this.id = id;
       }

       public String getUsername() {
           return username;
       }

       public void setUsername(String username) {
           this.username = username;
       }

       public String getPassword() {
           return password;
       }

       public void setPassword(String password) {
           this.password = password;
       }

       public String getEmail() {
           return email;
       }

       public void setEmail(String email) {
           this.email = email;
       }

       public String getPhone() {
           return phone;
       }

       public void setPhone(String phone) {
           this.phone = phone;
       }

       public String getAvatar() {
           return avatar;
       }

       public void setAvatar(String avatar) {
           this.avatar = avatar;
       }

       @Override
       public boolean isEnabled() {
           return enabled;
       }

       public void setEnabled(boolean enabled) {
           this.enabled = enabled;
       }

       public String getRole() {
           return role;
       }

       public void setRole(String role) {
           this.role = role;
       }

       public boolean isVerified() {
           return isVerified;
       }

       public void setVerified(boolean isVerified) {
           this.isVerified = isVerified;
       }

       // UserDetails methods
       @Override
       public Collection<? extends GrantedAuthority> getAuthorities() {
           if (role != null) {
               return List.of(new SimpleGrantedAuthority("ROLE_" + role));
           }
           return Collections.emptyList();
       }
       @Override
    public boolean isAccountNonExpired() {
        return true; // Giá trị mặc định
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Giá trị mặc định
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Giá trị mặc định
    }
   }