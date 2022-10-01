import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.restassured.RestAssured
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory

import java.lang.reflect.Type
import java.nio.file.Path
import java.nio.file.Paths

@Grapes([
        @GrabExclude('org.apache.groovy:groovy-xml'),
        @GrabExclude('org.apache.groovy:groovy-json'),
        @Grab(group='com.fasterxml.jackson.core', module='jackson-core', version='2.13.3'),
        @Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.13.3'),
        @Grab(group='com.fasterxml.jackson.core', module='jackson-annotations', version='2.13.3'),
        @Grab(group='io.rest-assured', module='rest-assured', version='5.1.1')
])
final ObjectMapper objectMapper = JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .addModule(new JavaTimeModule())
        .build()
RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
        ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {
            @Override
            ObjectMapper create(Type t, String charset) {
                objectMapper
            }
        })
)

final Path outputDir = Paths.get('generated')
outputDir.toFile().mkdirs()

final List<Card> cards = RestAssured.given().get('https://db.ygoprodeck.com/api/v7/cardinfo.php')
        .then().assertThat().statusCode(200).and().extract().jsonPath().getList('data', Card)
cards.each { card ->
    card.postprocess()
}

final List<CardSet> cardSets = RestAssured.given().get('https://db.ygoprodeck.com/api/v7/cardsets.php')
        .then().assertThat().statusCode(200).and().extract().jsonPath().getList('', CardSet).toList()

cardSets.sort { it.tcgDate }

objectMapper.readValue(new File('classifications.json'), SetClassification).classifySets(cardSets, cards)

objectMapper.writeValue(outputDir.resolve("cards_${System.currentTimeMillis()}.json").toFile(), cards)
objectMapper.writeValue(outputDir.resolve("sets_${System.currentTimeMillis()}.json").toFile(), cardSets)