package nmg.ms.groovy.showcase.resource

import nmg.ms.groovy.showcase.domain.SkuDetail
import nmg.ms.groovy.showcase.service.ProductService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@Slf4j
class SkuResource {
    // httpget at /products/{id} returns product json
    @Autowired
    ProductService productService


    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(method=RequestMethod.GET, value='/products/{productId}',produces = MediaType.APPLICATION_JSON_VALUE)
    public SkuDetail getSku(@PathVariable("productId") Integer productId){
        log.info("action=getSku::productId::${productId}")
        productService.getProductDetails(productId)
    }



}
