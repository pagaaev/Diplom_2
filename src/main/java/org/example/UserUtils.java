package org.example;

import org.apache.commons.lang3.RandomStringUtils;

public class UserUtils {

    public static User getRandomUser() {
        String email = RandomStringUtils.randomAlphanumeric(11) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphanumeric(12);
        String name = RandomStringUtils.randomAlphanumeric(7);
        User user = new User(email, password, name);
        return user;
    }
}
