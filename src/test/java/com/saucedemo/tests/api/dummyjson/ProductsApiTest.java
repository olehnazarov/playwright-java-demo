package com.saucedemo.tests.api.dummyjson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.saucedemo.base.BaseApiTest;
import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@Epic("DummyJSON API")
@Feature("Products")
@ExtendWith(AllureJunit5.class)
class ProductsApiTest extends BaseApiTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected String getBaseUrl() {
        return "https://dummyjson.com";
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET /products returns HTTP 200")
    @Story("Product list")
    void getProductsShouldReturn200() {
        assertStatus(get("/products"), 200);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET /products returns non-empty product list")
    @Story("Product list")
    void getProductsShouldReturnProducts() throws Exception {
        APIResponse response = get("/products");
        JsonNode body = mapper.readTree(response.text());

        assertTrue(body.has("products"), "Response should have 'products' field");
        assertFalse(body.get("products").isEmpty(), "Products list should not be empty");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /products — each product has required fields")
    @Story("Product schema")
    void eachProductShouldHaveRequiredFields() throws Exception {
        APIResponse response = get("/products");
        JsonNode products = mapper.readTree(response.text()).get("products");

        for (JsonNode product : products) {
            assertAll("Product fields",
                    () -> assertTrue(product.has("id"),    "Missing field: id"),
                    () -> assertTrue(product.has("title"), "Missing field: title"),
                    () -> assertTrue(product.has("price"), "Missing field: price"),
                    () -> assertTrue(product.has("stock"), "Missing field: stock")
            );
        }
    }

    @ParameterizedTest(name = "GET /products/{0} returns HTTP 200")
    @ValueSource(ints = {1, 2, 3})
    @Severity(SeverityLevel.NORMAL)
    @Story("Single product")
    void getProductByIdShouldReturn200(int id) {
        assertStatus(get("/products/" + id), 200);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /products/1 returns correct product data")
    @Story("Single product")
    void getProductByIdShouldReturnCorrectData() throws Exception {
        APIResponse response = get("/products/1");
        JsonNode product = mapper.readTree(response.text());

        assertEquals(1, product.get("id").asInt(), "Product id should be 1");
        assertFalse(product.get("title").asText().isBlank(), "Product title should not be blank");
        assertTrue(product.get("price").asDouble() > 0, "Product price should be positive");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /products/search?q=phone returns results")
    @Story("Product search")
    void searchProductsShouldReturnResults() throws Exception {
        APIResponse response = get("/products/search?q=phone");
        JsonNode body = mapper.readTree(response.text());

        assertTrue(body.has("products"), "Search response should have 'products' field");
        assertTrue(body.get("total").asInt() > 0, "Search should return at least one result");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /products/search?q=zzznoresults returns empty list")
    @Story("Product search")
    void searchWithNoMatchShouldReturnEmptyList() throws Exception {
        APIResponse response = get("/products/search?q=zzznoresults");
        JsonNode body = mapper.readTree(response.text());

        assertEquals(0, body.get("total").asInt(),
                "Search with no match should return total: 0");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /products?limit=5 returns exactly 5 products")
    @Story("Pagination")
    void limitParamShouldRestrictProductCount() throws Exception {
        APIResponse response = get("/products?limit=5");
        JsonNode products = mapper.readTree(response.text()).get("products");

        assertEquals(5, products.size(), "Should return exactly 5 products with limit=5");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /products/add creates a new product")
    @Story("Create product")
    void postProductShouldReturn201OrOk() throws Exception {
        String body = """
                {
                  "title": "Test Backpack",
                  "price": 29.99,
                  "stock": 10,
                  "category": "bags"
                }
                """;

        APIResponse response = post("/products/add", body);
        assertStatus(response, 200);

        JsonNode created = mapper.readTree(response.text());
        assertTrue(created.has("id"), "Created product should have an id");
        assertEquals("Test Backpack", created.get("title").asText());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /products/999 returns 404 for non-existent product")
    @Story("Error handling")
    void getNonExistentProductShouldReturn404() {
        assertStatus(get("/products/999"), 404);
    }
}
