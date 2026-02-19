package entities.filters;

import com.fasterxml.jackson.databind.JsonNode;

import static main.App.MAPPER;


/**
 * Utility class for parsing JSON input into Filter object
 * Provides static methods to convert JSON strings or JsonNodes into Filter instances
 * using Jackson's ObjectMapper.
 */
public class FilterParser {
    /**
     * Parses a JSON string into a Filter object
     * @param json the JSON string containing filter criteria
     * @return a Filter object populated with values from the JSON
     * @throws Exception if JSON parsing fails or the JSON is malformed
     */
    public static Filter parseFilter(final String json) throws Exception {
        return MAPPER.readValue(json, Filter.class);
    }

    /**
     * Parses a JsonNode into a Filter object
     * @param filterNode the JsonNode containing filter criteria
     * @return a Filter object populated with values from the JsonNode
     * @throws Exception if JSON conversion fails
     */
    public static Filter parseFilter(final JsonNode filterNode) throws Exception {
        return MAPPER.treeToValue(filterNode, Filter.class);
    }
}
