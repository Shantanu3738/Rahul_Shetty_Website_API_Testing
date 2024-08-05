import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;

public class ECommerceWeb {

    @Test
    public void AddItems() throws IOException {
        RequestSpecification reqspec = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON).build();

//        Properties prop = new Properties();
//        FileInputStream fis = new FileInputStream(
//                "D:\\Study\\GlobalProperties.properties");
//        prop.load(fis);
//
//        String email = prop.getProperty("Email");
//        String password = prop.getProperty("Password");

        LoginBodyPOJO loginBody = new LoginBodyPOJO();
        loginBody.setUserEmail("shaan@gmail.com");
        loginBody.setUserPassword("Bublaidey@1007");

        LoginResponsePOJO loginResponse = given().log().all().spec(reqspec)
                .body(loginBody)
                .when().post("/api/ecom/auth/login")
                .then().log().all().assertThat().statusCode(200).extract().response().as(LoginResponsePOJO.class);

        String token = loginResponse.getToken();
        System.out.println(token);

        String productID = "6581ca979fd99c85e8ee7faf";

        OrderBodyPOJO orderBody = new OrderBodyPOJO();          //Push in values in Orders Body ethod to add
        orderBody.setProductOrderedId(productID);
        orderBody.setCountry("India");

        List<OrderBodyPOJO> orderList = new ArrayList<OrderBodyPOJO>(); //Create List becuase Order Body has a list of values
        orderList.add(orderBody); //Add order body to the list to send to Add Item Cart

        AddItemToCartPOJO addItem = new AddItemToCartPOJO();
        addItem.setOrders(orderList);   //Add list item to cart body

        String response = given().log().all().spec(reqspec)
                .header("Authorization",token)
                .body(addItem)
                .when().post("/api/ecom/order/create-order")
                .then().log().all().assertThat().statusCode(201).extract().response().asString();

        JsonPath js = new JsonPath(response);
        String orderID = js.getString("orders[0]");

        RequestSpecification deleteItemSpec = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .build();

        given().log().all().spec(deleteItemSpec)
                .header("Authorization",token)
                .pathParams("_id", orderID)
                .when().delete("/api/ecom/order/delete-order/{_id}")
                .then().log().all().assertThat().statusCode(200);
    }

}
