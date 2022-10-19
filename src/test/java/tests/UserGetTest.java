package tests;

import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get user data cases")
@Feature("Get user data")
public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test checks that for not authorization user we can get only username")
    @DisplayName("Test negative get data for not authorization user")
    @Severity(SeverityLevel.MINOR)
    @Link("https://example.org")
    @Link(name = "allure", type = "mylink")
    @Issue("235")
    @TmsLink("test-14")
    public void testGetUserDataNotAuth (){

        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(
                "https://playground.learnqa.ru/api/user/2");

        String[] unexpectedFieldNames = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFieldNames);
    }

    @Test
    @Description("This test checks that for authorization user we can get all his data")
    @DisplayName("Test positive get data for authorization user")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://example.org")
    @Link(name = "allure", type = "mylink")
    @Issue("245")
    @TmsLink("test-15")
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie);

        String[] expectedFieldNames = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFieldNames);
    }

    @Test
    @Description("This test checks that an authorized user cannot get another not authorized user's data")
    @DisplayName("Test authorization user negative gets data for not authorization user")
    @Severity(SeverityLevel.MINOR)
    @Link("https://example.org")
    @Link(name = "allure", type = "mylink")
    @TmsLink("test-16")
    public void testGetUserDetailsAuthAsAnotherUser(){

        //Create a new user
        Map<String, String> newUserData = DataGenerator.getRegistrationData();
        JsonPath responseCreateUser = apiCoreRequests.makePostJsonRequest(
                "https://playground.learnqa.ru/api/user",
                newUserData);

        //Get user id
        String userId = responseCreateUser.getString("id");

       //Authorization for another user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");
        int userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");

        //Check that authorization is done
        Response responseCheckAuth = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/auth",
                header,
                cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", userIdOnAuth);

        //Get data for the first not authorization user
        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId);

        //Сhecks
        String[] unexpectedFieldNames = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFieldNames);
    }
}
