package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("User creation cases")
@Feature("User creation")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test checks that it is not possible to create two users with the same email")
    @DisplayName("Test negative creation user with existing email")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("This test successfully create user")
    @DisplayName("Test positive user create")
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("This test checks that it is not possible to create a user with an email address " +
            "that does not contain the symbol @")
    @DisplayName("Test negative creation user with email without @")
    public void testCreateUserWithIncorrectEmail() {
        String email = "wrongemail.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @Test
    @Description("This test checks that it is not possible to create a user with a single " +
            "character name")
    @DisplayName("Test negative creation user with a single character name")
    public void testCreateUserWithOneSymbolName() {
        String username = "Ф";
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Test
    @Description("This test checks that it is not possible to create a user with a name longer than " +
            "two hundred and fifty characters")
    @DisplayName("Test negative creation user with a name longer than 250 characters")
    public void testCreateUserWithVeryLongName() {
        Map<String, String> userData = new HashMap<>();
        String username = "testname";
        int lenght = username.length();

        if (lenght <= 250) {
            do {
                username = username + username;
                lenght = username.length();
            } while (lenght <= 250);
        }
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "email", "password", "firstName", "lastName"})
    @Description("This test checks that it is not possible to create a user without one of parameters")
    @DisplayName("Test negative creation user without one of parameters")
    public void testCreateUserWithoutOneOfParameters(String condition) {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        if(condition.equals("username") || condition.equals("email") ||
                condition.equals("password") || condition.equals("firstName") ||
                condition.equals("lastName")) {
            userData.remove(condition);
        }else {
            throw new IllegalArgumentException("Condition value is unknown: " + condition);
        }

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + condition);
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "email", "password", "firstName", "lastName"})
    @Description("This test checks that it is not possible to create a user with one empty parameter")
    @DisplayName("Test negative creation user without one of parameters")
    public void testCreateUserWithOneEmptyParameter(String condition) {
        Map<String, String> userData = new HashMap<>();
        String emptyValue = "";

        if(condition.equals("username") || condition.equals("email") ||
                condition.equals("password") || condition.equals("firstName") ||
                condition.equals("lastName")) {
            userData.put(condition, emptyValue);;
        }else {
            throw new IllegalArgumentException("Condition value is unknown: " + condition);
        }

        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of '" + condition + "' field is too short");
    }
}
