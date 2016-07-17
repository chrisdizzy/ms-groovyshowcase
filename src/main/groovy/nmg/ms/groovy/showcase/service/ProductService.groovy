package nmg.ms.groovy.showcase.service

import nmg.ms.groovy.showcase.domain.error.ErrorResponse
import nmg.ms.groovy.showcase.exception.ProductServiceException
import nmg.ms.groovy.showcase.util.RestTemplateBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.gpars.GParsPool
import nmg.ms.groovy.showcase.domain.SkuDetail
import nmg.ms.groovy.showcase.domain.error.ErrorBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

import javax.annotation.PostConstruct
import java.util.concurrent.Future

@Slf4j
@Component
class ProductService {

    @Autowired
    RestTemplateBuilder restTemplateBuilder

    RestTemplate restTemplate

    @Value('${productApi.url}')
    String productApiUrl

    @Value('${productApi.requestUri.fields}')
    String reqUriFields

    @Value('${productApi.requestUri.id_type}')
    String reqUriIdType

    @Value('${productApi.requestUri.key}')
    String reqUriKey

    @PostConstruct
    void init(){
        restTemplate = restTemplateBuilder.buildRestTemplate()
    }

    public SkuDetail getProductDetails(Integer productId){
        log.info("action=getProductDetails::productId:${productId}")
        Future<SkuDetail> productMapFromApi= null
        SkuDetail skuDetail = null
        long startTime = System.currentTimeMillis()
        try{
                GParsPool.withPool {
                productMapFromApi = { getProductDetailsFromApi(productId) }.callAsync()
            }
            skuDetail= productMapFromApi.get()
        }catch(Exception e){
            if(e.getCause() instanceof ProductServiceException){
                throw e
            }
            log.error("Exception occured::action=getproductDetails()::exception:${e.message}",e)
            skuDetail = new SkuDetail(errorResponse: new ErrorResponse(errors : [new ErrorBody(errorCode: "Internal Server Error",errorMsg: "Exception from base services, please try later")]))
            log.info("action=getproductDetails exception exit::productId:${productId}::skudetails:${skuDetail}")
        }


    return skuDetail

    }

    public SkuDetail getProductDetailsFromApi(Integer productId){
        HttpEntity httpEntity = new HttpEntity(getHeaders())
        Map map =["fields":reqUriFields,"id_type":reqUriIdType,"key":reqUriKey,"productId":productId.toString()]
        ResponseEntity responseEntity = restTemplate.exchange(productApiUrl, HttpMethod.GET, httpEntity, String.class,map)
        SkuDetail skuDetail = null
        if(responseEntity?.getBody()){
            def result = new JsonSlurper().parseText(responseEntity.getBody())
            def item = result?.product_composite_response?.items?.find{it.identifier*.id?.contains(productId as String)}
            if(item){
                skuDetail= new SkuDetail()
                skuDetail.skuId = productId
                skuDetail.skuName = item?.online_description?.value
            }else{
                def errorList = item?.errors
                def messages = errorList*.message
                skuDetail = new  SkuDetail(errorResponse: new ErrorResponse(errors : [new ErrorBody(errorCode: "Product Not Found",errorMsg: messages?.join('::'))]))
            }
        }
        return skuDetail

    }

    private HttpHeaders getHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders()
        httpHeaders.add("Content-Type",MediaType.APPLICATION_JSON_VALUE)
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE)
        return httpHeaders
    }


}
