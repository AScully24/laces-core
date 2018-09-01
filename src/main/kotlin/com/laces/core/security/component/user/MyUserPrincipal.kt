package com.laces.core.security.component.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class MyUserPrincipal(val user: User) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return null
    }

    override fun isEnabled(): Boolean {
        return user.isActive
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return user.subscriptionActive
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun toString(): String {
        return "MyUserPrincipal(user=$user)"
    }


}