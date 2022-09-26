import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LectureThreeHWTest {
    @ParameterizedTest
    @ValueSource(strings = {"short phrase", "a phrase with more than fifteen characters"})
    public void testShortPhraseCheck(String phrase) {
        assertTrue(phrase.length() > 15,
                "Phrase length must be more than 15 characters");
    }

    @Test
    public void testHomeworkCookie() {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookies = response.getCookies();
        assertTrue(cookies.containsKey("HomeWork"),
                "Response doesn't have 'HomeWork' cookie");

        String homeworkCookie = response.getCookie("HomeWork");
        assertEquals(
                homeworkCookie,
                "hw_value",
                "Cookie HomeWork has wrong value: " + homeworkCookie);
    }

    @Test
    public void testHomeworkHeader() {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        Headers responseHeaders = response.getHeaders();

        assertTrue(responseHeaders.hasHeaderWithName("x-secret-homework-header"),
                "Response doesn't have 'x-secret-homework-header' header");

        String homeworkHeader = response.getHeader("x-secret-homework-header");
        assertEquals(
                homeworkHeader,
                "Some secret value",
                "Header 'x-secret-homework-header' has wrong value: " + homeworkHeader);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30!Mobile!No!Android",
            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1!Mobile!Chrome!iOS",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)!Googlebot!Unknown!Unknown",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0!Web!Chrome!No",
            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1!Mobile!No!iPhone"})
    public void testNegativeAuthUser(String userAgent) {
        List<String> params = Arrays.asList(userAgent.split("!"));
        JsonPath response = RestAssured
                .given()
                .header("User-Agent", params.get(0))
                .when()
                .get("https://playground.learnqa.ru/api/user_agent_check")
                .jsonPath();

        String platform = response.get("platform");
        String browser = response.get("browser");
        String device = response.get("device");

        assertEquals(
                params.get(1),
                platform,
                "User Agent '" + params.get(0) + "' has wrong value platform: " + platform);

        assertEquals(
                params.get(2),
                browser,
                "User Agent '" + params.get(0) + "' has wrong value browser: " + browser);

        assertEquals(
                params.get(3),
                device,
                "User Agent '" + params.get(0) + "' has wrong value device: " + device);

        System.out.println(params.get(0) + " has right parameters");
    }
}
