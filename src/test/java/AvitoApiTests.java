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
                .body("name", equalTo("Телефон"))
                .body("price", equalTo(85566));
    }

    @Test
    public void testGetAdById() {
        int adId = 1; // Замените на ID существующего объявления

        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("https://qa-internship.avito.com/api/1/item/" + adId);

        response.then().statusCode(200)
                .body("id", equalTo(adId))
                .body("name", equalTo("Телефон")); // Проверьте реальное имя товара
    }

    @Test
    public void testUpdateAd() {
        int adId = 1; // Замените на ID существующего объявления
        String updatedRequestBody = "{ \"name\": \"Ноутбук\", \"price\": 95000, \"sellerId\": 3452, \"statistics\": { \"contacts\": 50, \"like\": 45, \"viewCount\": 25 }}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(updatedRequestBody)
                .when()
                .put("https://qa-internship.avito.com/api/1/item/" + adId);

        response.then().statusCode(200)
                .body("name", equalTo("Ноутбук"))
                .body("price", equalTo(95000));
    }

    @Test
    public void testDeleteAd() {
        int adId = 1; // Замените на ID существующего объявления

        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .delete("https://qa-internship.avito.com/api/1/item/" + adId);

        response.then().statusCode(204);
    }

    @Test
    public void testCreateAdWithInvalidData() {
        String invalidRequestBody = "{ \"name\": \"\", \"price\": -1000, \"sellerId\": 3452, \"statistics\": { \"contacts\": 32, \"like\": 35, \"viewCount\": 14 }}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(invalidRequestBody)
                .when()
                .post("https://qa-internship.avito.com/api/1/item");

        response.then().statusCode(400)
                .body("error", equalTo("Invalid request data"));
    }

    @Test
    public void testGetAllAds() {
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("https://qa-internship.avito.com/api/1/items");

        response.then().statusCode(200)
                .body("items", hasSize(10)); // Предполагаем, что есть хотя бы 10 объявлений
    }

    @Test
    public void testGetNonExistingAd() {
        int nonExistingAdId = 9999;

        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("https://qa-internship.avito.com/api/1/item/" + nonExistingAdId);

        response.then().statusCode(404)
                .body("error", equalTo("Ad not found"));
    }

    @Test
    public void testCreateAdWithoutPrice() {
        String requestBodyWithoutPrice = "{ \"name\": \"Телефон\", \"sellerId\": 3452, \"statistics\": { \"contacts\": 32, \"like\": 35, \"viewCount\": 14 }}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBodyWithoutPrice)
                .when()
                .post("https://qa-internship.avito.com/api/1/item");

        response.then().statusCode(400)
                .body("error", equalTo("Price is required"));
    }
}
