import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
class Card {

    @JsonInclude(JsonInclude.Include.NON_NULL) Integer id
    List<Integer> ids
    String name
    String type
    List<CardSetMinimum> cardSets
    @JsonInclude(JsonInclude.Include.NON_NULL) List<CardImage> cardImages

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
    static class CardSetMinimum {
        @JsonValue String setName
    }

    static class CardImage {
        @JsonValue Integer id
    }

    void postprocess() {
        ids = ([id] + cardImages*.id).unique() as List<Integer>
        id = null
        cardImages = null
    }

}
