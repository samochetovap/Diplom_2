package builder;

import io.qameta.allure.Step;
import pojo.User;

import java.util.UUID;

public class UserBuilder {

    @Step("Генерация рандомного тестового юзера")
    public static User generateUser(){
        return new User(UUID.randomUUID() + "@somemail.com", "somepassword12345", UUID.randomUUID()  + "someUserName");
    }

}
