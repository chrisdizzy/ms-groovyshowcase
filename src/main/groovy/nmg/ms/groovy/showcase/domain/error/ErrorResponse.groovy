package nmg.ms.groovy.showcase.domain.error

import groovy.transform.ToString

@ToString
class ErrorResponse {
    List<ErrorBody> errors
}
