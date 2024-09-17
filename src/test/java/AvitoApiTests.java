import static org.hamcrest.Matchers.hasSize;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AvitoApiTests {

    @Test
    public void testCreateAd() {
        String requestBody = "{ \"name\": \"Телефон\", \"price\": 85566, \"sellerId\": 3452, \"statistics\": { \"contacts\": 32, \"like\": 35, \"viewCount\": 14 }}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("https://qa-internship.avito.com/api/1/item");

        response.then().statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Телефон"))
                .body("price", equalTo(85566));
    }

    @Test
    public void testGetAdById() {
        String requestBody = "{ \"name\": \"Телефон\", \"price\": 85566, \"sellerId\": 3452 }";
        int adId = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("https://qa-internship.avito.com/api/1/item")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .header("Content-Type", "application/json")
                .when()
                .get("https://qa-internship.avito.com/api/1/item/" + adId)
                .then()
                .statusCode(200)
                .body("id", equalTo(adId))
                .body("name", equalTo("Телефон"));
    }

    @Test
    public void testGetNonExistingAd() {
        int nonExistingAdId = 9999;

        given()
                .header("Content-Type", "application/json")
                .when()
                .get("https://qa-internship.avito.com/api/1/item/" + nonExistingAdId)
                .then()
                .statusCode(404)
                .body("error", equalTo("Ad not found"));
    }

    @Test
    public void testGetAllAdsBySeller() {
        int sellerId = 3452; 
        given()
                .header("Content-Type", "application/json")
                .when()
                .get("https://qa-internship.avito.com/api/1/" + sellerId + "/item")
                .then()
                .statusCode(200)
                .body("items", hasSize(0)); 

    @Test
    public void testCreateAdWithInvalidData() {
        String invalidRequestBody = "{ \"name\": \"\", \"price\": -1000, \"sellerId\": 3452, \"statistics\": { \"contacts\": 32, \"like\": 35, \"viewCount\": 14 }}";

        given()
                .header("Content-Type", "application/json")
                .body(invalidRequestBody)
                .when()
                .post("https://qa-internship.avito.com/api/1/item")
                .then()
                .statusCode(400)
                .body("error", equalTo("Invalid request data"));
    }
}
