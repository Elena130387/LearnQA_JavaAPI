package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Edit user data cases")
@Feature("Edit user data")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    Map<String, String> userData;
    String userId;

    @BeforeEach
    public void createUser() {
        userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostJsonRequest(
                "https://playground.learnqa.ru/api/user",
                userData);

        userId = responseCreateAuth.getString("id");
    }

    @Test
    @Description("This test checks that for authorization user we can change the firstName")
    @DisplayName("Test positive change firstName for authorization user")
    public void testEditJustCreatedUser() {

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        //EDIT
        String newName = "Change name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                editData,
                header,
                cookie);

        //GET

        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("This test checks that for not authorization user we can not change data")
    @DisplayName("Test negative change username for not authorization user")
    public void testEditJustCreatedUserWithoutAuth() {

        //EDIT
        String newName = "Change name";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithoutTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId);

        Assertions.assertJsonByName(responseUserData, "username", userData.get("username"));
    }

    @Test
    @Description("This test checks that we cannot change the data for an unauthorized user by first logging in as a different user ")
    @DisplayName("Test negative change username by authorization user for not authorization user")
    public void testEditJustCreatedUserByAnotherUserAuth() {

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);
        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        //EDIT
        String newName = "Change name";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                editData,
                header,
                cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId);

        Assertions.assertJsonByName(responseUserData, "username", userData.get("username"));
    }

    @Test
    @Description("This test checks that we cannot change email to incorrect for authorization user")
    @DisplayName("Test negative change email by authorization user to incorrect value")
    public void testChangeUserEmailToIncorrect() {

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);
        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        //EDIT
        String wrongEmail= "wrongemail.com";;
        Map<String, String> editData = new HashMap<>();
        editData.put("email", wrongEmail);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                editData,
                header,
                cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie);

        Assertions.assertJsonByName(responseUserData, "email", userData.get("email"));
    }

    @Test
    @Description("This test checks that we cannot change name for authorization user to the name with one symbol")
    @DisplayName("Test negative change name by authorization user to the name with one symbol")
    public void testChangeUserNameToNameWithOneSymbol() {

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);
        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        //EDIT
        String newName = "u";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                editData,
                header,
                cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Too short value for field firstName\"}");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }
}
