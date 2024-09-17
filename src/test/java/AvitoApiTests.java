import static org.hamcrest.Matchers.hasSize;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;


public class AvitoApiTests {

    @Test
    public void testCreateAd() {
        String requestBody = "{ \"name\": \"Телефон\", \"price\": 85566, \"sellerId\": 3452, \"statistics\": { \"contacts\": 32, \"like\": 35, \"viewCount\": 14 }}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("https://qa-internship.avito.com/api/1/item");

        response.then().statusCode(200);

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        String adId = responseBody.split(" - ")[1].replace("\"}", "");

        assertThat(adId, notNullValue());
        System.out.println("ID объявления: " + adId);
    }

    //todo
    //bug in system
    @Test
    public void testGetAdById() {
        // Создаем объявление и извлекаем id из ответа
        String requestBody = "{ \"name\": \"Телефон\", \"price\": 85566, \"sellerId\": 3452 }";
        Response createResponse = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("https://qa-internship.avito.com/api/1/item")
                .then()
                .statusCode(200) // Ожидаем статус 200, так как API возвращает 200, а не 201
                .extract()
                .response();

        // Извлекаем идентификатор из сообщения "Сохранили объявление - <id>"
        String statusMessage = createResponse.path("status");
        String adId = statusMessage.split(" - ")[1];

        given()
                .header("Content-Type", "application/json")
                .when()
                .get("https://qa-internship.avito.com/api/1/item/" + adId)
                .then()
                .statusCode(200)
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
                .body("result.message", equalTo("item 9999 not found"));
    }

    @Test
    public void testGetAllAdsBySeller() {
        int sellerId = 3452;

        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("https://qa-internship.avito.com/api/1/" + sellerId + "/item")
                .thenReturn();

        System.out.println("Response: " + response.getBody().asString());

        response.then().statusCode(200)
                .body("size()", greaterThan(0));
    }

    //todo
    //bug in system
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

    @Test
    public void testUpdateAd() {
        String requestBody = "{ \"name\": \"Смартфон\", \"price\": 50000, \"sellerId\": 1234 }";
        int adId = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("https://qa-internship.avito.com/api/1/item")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateRequestBody = "{ \"name\": \"Обновленный смартфон\", \"price\": 60000 }";

        given()
                .header("Content-Type", "application/json")
                .body(updateRequestBody)
                .when()
                .put("https://qa-internship.avito.com/api/1/item/" + adId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Обновленный смартфон"))
                .body("price", equalTo(60000));
    }

    @Test
    public void testUpdateNonExistingAd() {
        int nonExistingAdId = 9999;

        String updateRequestBody = "{ \"name\": \"Обновленный смартфон\", \"price\": 60000 }";

        given()
                .header("Content-Type", "application/json")
                .body(updateRequestBody)
                .when()
                .put("https://qa-internship.avito.com/api/1/item/" + nonExistingAdId)
                .then()
                .statusCode(404)
                .body("error", equalTo("Ad not found"));
    }

    @Test
    public void testDeleteAd() {
        String requestBody = "{ \"name\": \"Ноутбук\", \"price\": 70000, \"sellerId\": 5678 }";
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
                .delete("https://qa-internship.avito.com/api/1/item/" + adId)
                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteNonExistingAd() {
        int nonExistingAdId = 9999;

        given()
                .header("Content-Type", "application/json")
                .when()
                .delete("https://qa-internship.avito.com/api/1/item/" + nonExistingAdId)
                .then()
                .statusCode(404)
                .body("error", equalTo("Ad not found"));
    }

    @Test
    public void testCreateAdWithEmptyBody() {
        String emptyRequestBody = "{}";

        given()
            .header("Content-Type", "application/json")
            .body(emptyRequestBody)
            .when()
            .post("https://qa-internship.avito.com/api/1/item")
            .then()
            .statusCode(400)
            .body("error", equalTo("Invalid request data"));
    }

    @Test
    public void testCreateAdWithoutName() {
        String requestBodyWithoutName = "{ \"price\": 50000, \"sellerId\": 1234 }";

        given()
            .header("Content-Type", "application/json")
            .body(requestBodyWithoutName)
            .when()
            .post("https://qa-internship.avito.com/api/1/item")
            .then()
            .statusCode(400)
            .body("error", equalTo("Name is required"));
    }

    @Test
    public void testUpdateAdWithInvalidPrice() {
        String requestBody = "{ \"name\": \"Товар\", \"price\": 50000, \"sellerId\": 1234 }";
        int adId = given()
            .header("Content-Type", "application/json")
            .body(requestBody)
            .post("https://qa-internship.avito.com/api/1/item")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

        String updateRequestBody = "{ \"price\": -5000 }";

        given()
            .header("Content-Type", "application/json")
            .body(updateRequestBody)
            .when()
            .put("https://qa-internship.avito.com/api/1/item/" + adId)
            .then()
            .statusCode(400)
            .body("error", equalTo("Invalid price"));
    }

    @Test
    public void testGetAllAdsForSellerWithManyAds() {
        int sellerId = 1234;

        given()
            .header("Content-Type", "application/json")
            .when()
            .get("https://qa-internship.avito.com/api/1/" + sellerId + "/item")
            .then()
            .statusCode(200)
            .body("items.size()", greaterThan(100));
    }
}
