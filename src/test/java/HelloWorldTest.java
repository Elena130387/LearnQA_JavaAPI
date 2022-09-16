import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void testHelloWorld() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        System.out.println(response.asString());
        response.body().print();
    }

    @Test
    public void testRestAssured() {
        Map<String, String> params = new HashMap<>();
        params.put("name","Helena");
        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String answer = response.get("answer2");
        if (answer == null){
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(answer);
        }
    }
    @Test
    public void testRestAssuredPost() {
        Response response = RestAssured
                .given()
                .body("param1=value1&param2=value2") // "сырой" способ передачи
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();

        Response responseJSON = RestAssured
                .given()
                .body("{\"param3\":\"value3\",\"param4\":\"value4\"}") // JSON способ передачи
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        responseJSON.print();

        // Map<> передает данные в JSON формате, как в примере для responseJSON
        Map<String, String> body = new HashMap<>();
        body.put("param5","value5");
        body.put("param6","value6");

        Response responseBody = RestAssured
                .given()
                .body(body) // JSON способ передачи
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        responseBody.print();
    }

    @Test
    public void testRestAssuredRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();
        int statusCode = response.statusCode();
        System.out.println(statusCode);

        response.prettyPrint();
        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testRestAssuredHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("header1","value5");
        headers.put("header2","value6");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();

        response.prettyPrint();

        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);
    }

    @Test
    public void testRestAssuredCookiesPrint() {
        Map<String, String> data = new HashMap<>();
        data.put("login","secret_login");
        data.put("password","secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        System.out.println("\nPretty text:");
        response.prettyPrint();

        System.out.println("\nHeaders:");
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);

        System.out.println("\nCookies:");
        Map<String, String> responseCookies = response.getCookies();
        System.out.println(responseCookies);

        String responseCookie = response.getCookie("auth_cookie");
        System.out.println(responseCookie);
    }

    @Test
    public void testRestAssuredCookiesSend() {
        Map<String, String> data = new HashMap<>();
        data.put("login","secret_login");
        data.put("password","secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = responseForGet.getCookie("auth_cookie");
        Map<String, String> cookies = new HashMap<>();

        if(responseCookie != null){
            cookies.put("auth_cookie", responseCookie);
        }

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();
    }
}
