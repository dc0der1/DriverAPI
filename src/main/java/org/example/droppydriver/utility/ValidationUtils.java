package org.example.droppydriver.utility;

import java.util.UUID;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean isValidUUID(String uuid) {
        if (uuid == null) return false;
        return UUID_PATTERN.matcher(uuid).matches();
    }

}
