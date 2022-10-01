import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

import java.time.LocalDate

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
class CardSet {

    String setName
    String setCode
    int numOfCards
    LocalDate tcgDate
    @JsonInclude(JsonInclude.Include.NON_NULL) Boolean centerValid
    @JsonInclude(JsonInclude.Include.NON_NULL) String setType

    CardSet markCenterValid() {
        centerValid = true
        this
    }

    CardSet type(String type) {
        setSetType(type)
        this
    }

}
