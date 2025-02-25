package models;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {

    public static User getRandomUser() {
        String email = getRandomEmail();
        String password = getRandomPassword();
        String name = getRandomName();
        return new User.Builder()
                .withEmail(email)
                .withPassword(password)
                .withName(name)
                .build();
    }

    public static String getRandomEmail() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@test.com";
    }

    public static String getRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public static String getRandomName() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static UserCredentials getUserCredentials(User user) {
        return new UserCredentials(user.getEmail(), user.getPassword());
    }
}
