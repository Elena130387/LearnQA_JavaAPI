import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class RestAssuredHWTest {
    @Test
    public void testJsonParsing() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String secondMessage = response.get("messages[1].message");
        System.out.println(secondMessage);
    }

    @Test
    public void testRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testLongRedirect() {
        String URL = "https://playground.learnqa.ru/api/long_redirect";
        int responseStatus;
        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(URL)
                    .andReturn();
            responseStatus = response.statusCode();
            URL = response.getHeader("Location");
            if (URL != null) {
                System.out.println(URL);
            }
        } while (responseStatus != 200);
    }

    @Test
    public void testToken() throws InterruptedException {
        Map<String, String> headersToken = new HashMap<>();

        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String responseToken = response.get("token");
        int responseSeconds = response.get("seconds");
        responseSeconds = responseSeconds * 1000;

        headersToken.put("token", responseToken);
        RestAssured
                .given()
                .params(headersToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .then()
                .assertThat()
                .body("status", equalTo("Job is NOT ready"));

        sleep(responseSeconds);

        RestAssured
                .given()
                .params(headersToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .then()
                .assertThat()
                .body("status", equalTo("Job is ready"))
                .assertThat()
                .body("result", notNullValue());
    }

    @Test
    public void testCorrectPassword() throws IOException {
        String login = "super_admin";
        BufferedReader reader = new BufferedReader(new FileReader(new File("popularPasswords.crv")));
        String password;
        String answer;
        do {
            password = reader.readLine();
            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", password);
            Response response = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            String responseCookie = response.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            if (responseCookie != null) {
                cookies.put("auth_cookie", responseCookie);
            }

            Response checkAuthCookie = RestAssured
                    .given()
                    .body(data)
                    .cookies(cookies)
                    .when()
                    .post("https://playground.learnqa.ru/api/check_auth_cookie")
                    .andReturn();
            answer = checkAuthCookie.asString();

            if (!answer.equals("You are NOT authorized")){
                System.out.println(answer);
                System.out.println(password);
                break;
            }
        } while (!Objects.isNull(password));
    }
}


