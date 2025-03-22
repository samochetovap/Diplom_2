package api;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import io.restassured.builder.RequestSpecBuilder;
public abstract class AbstractApi {
    protected static final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setBaseUri(ApiPaths.BASE_URL)
            .build();
}
