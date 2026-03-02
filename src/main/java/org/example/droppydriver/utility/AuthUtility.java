package org.example.droppydriver.utility;

import org.example.droppydriver.models.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AuthUtility {

    public static String currentUser() {
        var auth = Objects.requireNonNull(SecurityContextHolder.getContext()).getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();

        if (principal instanceof org.example.droppydriver.models.User user) {
            return user.getEmail();
        }

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return userDetails.getUsername();
        }

        assert principal != null;
        return principal.toString();
    }

}
