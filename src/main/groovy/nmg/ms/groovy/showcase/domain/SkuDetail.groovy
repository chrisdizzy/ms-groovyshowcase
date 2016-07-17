package nmg.ms.groovy.showcase.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import nmg.ms.groovy.showcase.domain.error.ErrorResponse
import groovy.transform.ToString

@JsonPropertyOrder(["skuId","skuName"])
@ToString
class SkuDetail {

    @JsonProperty("id")
    String skuId

    @JsonProperty("name")
    String skuName

    ErrorResponse errorResponse
}
