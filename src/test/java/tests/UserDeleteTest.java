package tests;


import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete user data cases")
@Feature("Delete user data")
public class UserDeleteTest {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test checks that we can not delete user with id = 2")
    @DisplayName("Test negative delete authorization user with id = 2")
    @Link("https://example.org")
    @Link(name = "allure", type = "mylink")
    @Severity(SeverityLevel.CRITICAL)
    public void testNotDeleteUserWithIdEqualsTwo() {
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie);

        String[] expectedFieldNames = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFieldNames);
    }

    @Test
    @Description("This test checks that we can delete authorization user")
    @DisplayName("Test positive delete authorization user")
    @Issue("123")
    @TmsLink("test-2")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteJustCreatedUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostJsonRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie);

        String[] unexpectedFieldNames = {"firstName", "lastName", "email"};
        Assertions.assertJsonByName(responseUserData, "username", "Vitaliy");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFieldNames);
    }

    @Test
    @Description("This test checks that we can not delete unauthorized user by first logging in as a different user")
    @DisplayName("Test negative delete unauthorization user by authorization user")
    @TmsLink("test-1")
    @Issue("125")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteJustCreatedUserByAnotherUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostJsonRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId);

        Assertions.assertJsonByName(responseUserData, "username", userData.get("username"));
    }
}
