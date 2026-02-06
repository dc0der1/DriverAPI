package org.example.droppydriver.utility;

import org.example.droppydriver.models.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AuthUtility {

    public static String currentUser() {

        var auth = Objects.requireNonNull(SecurityContextHolder.getContext()).getAuthentication();
        assert auth != null;
        if (auth.getPrincipal() instanceof User userDetails) {
            return userDetails.getEmail();
        }

        return null;

    }

}
