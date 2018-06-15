package com.mikechoch.prism.helper;

import android.support.design.widget.TextInputLayout;
import android.util.Patterns;

import java.util.Random;
import java.util.regex.Pattern;

public class ProfileHelper {


    /**
     * Runs the string fullName through several checks to verify it is valid
     * Returns True if validation passes otherwise displays error and returns False
     * @param fullName - String fullName to check for validation
     * @param fullNameTextInputLayout - TextInputLayout to display error
     */
    public static boolean isFullNameValid(String fullName, TextInputLayout fullNameTextInputLayout) {
        if (fullName.length() < 2) {
            fullNameTextInputLayout.setError("Name must be at least 2 characters long");
            return false;
        }
        if (fullName.length() > 70) {
            fullNameTextInputLayout.setError("Name cannot be longer than 70 characters");
            return false;
        }
        if (!Pattern.matches("^[a-zA-Z ']+", fullName)) {
            fullNameTextInputLayout.setError("Name can only contain letters, space, and apostrophe");
            return false;
        }
        if (Pattern.matches(".*(.)\\1{3,}.*", fullName)) {
            fullNameTextInputLayout.setError("Name cannot contain more than 3 repeating characters");
            return false;
        }
        if (Pattern.matches(".*(['])\\1{1,}.*", fullName)) {
            fullNameTextInputLayout.setError("Name cannot contain more than 1 apostrophe");
            return false;
        }
        if (!Character.isAlphabetic(fullName.charAt(0))) {
            fullNameTextInputLayout.setError("Name must start with a letter");
            return false;
        }
        if (fullName.endsWith("'")) {
            fullNameTextInputLayout.setError("Name must end with a letter");
            return false;
        }
        fullNameTextInputLayout.setErrorEnabled(false);
        return true;
    }

    /**
     * Runs the string username through several checks to verify it is valid
     * Returns True if validation passes otherwise displays error and returns False
     * @param username - String username to check for validation
     * @param usernameTextInputLayout - TextInputLayout to display error
     */
    public static boolean isUsernameValid(String username, TextInputLayout usernameTextInputLayout) {
        if (username.length() < 5) {
            usernameTextInputLayout.setError("Username must be as least 5 characters long");
            return false;
        }
        if (username.length() > 30) {
            usernameTextInputLayout.setError("Username cannot be longer than 30 characters");
            return false;
        }
        if (!Pattern.matches("^[a-z0-9._']+", username)) {
            usernameTextInputLayout.setError("Username can only contain lowercase letters, numbers, period, and underscore");
            return false;
        }
        if (Pattern.matches(".*([a-z0-9])\\1{5,}.*", username)) {
            usernameTextInputLayout.setError("Username cannot contain more than 3 repeating characters");
            return false;
        }
        if (Pattern.matches(".*([._]){2,}.*", username)) {
            usernameTextInputLayout.setError("Username cannot contain more than 1 repeating symbol");
            return false;
        }
        if (!Character.isAlphabetic(username.charAt(0))) {
            usernameTextInputLayout.setError("Username must start with a letter");
            return false;
        }
        if (username.endsWith("_") || username.endsWith(".")) {
            usernameTextInputLayout.setError("Username must end with a letter or number");
            return false;
        }
        usernameTextInputLayout.setErrorEnabled(false);
        return true;

    }

    /**
     * Runs the string email through several checks to verify it is valid
     * Returns True if validation passes otherwise displays error and returns False
     * @param email - String email to check for validation
     * @param emailTextInputLayout - TextInputLayout to display error
     */
    public static boolean isEmailValid(String email, TextInputLayout emailTextInputLayout) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.setErrorEnabled(false);
            return true;
        } else {
            emailTextInputLayout.setError("Invalid email");
            return false;
        }
    }

    /**
     * Runs the string password through several checks to verify it is valid
     * Returns True if validation passes otherwise displays error and returns False
     * @param password - String password to check for validation
     * @param passwordTextInputLayout - TextInputLayout to display error
     */
    public static boolean isPasswordValid(String password, TextInputLayout passwordTextInputLayout) {
        // TODO: Add more checks for valid password?
        if (password.length() > 5) {
            passwordTextInputLayout.setErrorEnabled(false);
            return true;
        } else {
            passwordTextInputLayout.setError("Password must be at least 6 characters long");
            return false;
        }
    }

    /**
     * Generate a random Default profile picture
     */
    public static String generateDefaultProfilePic() {
        // TODO @mike this '10' should be replaced with DefaultProfilePictures.values().length right?c
        return String.valueOf(new Random().nextInt(10));
    }

}
