package entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class holding details of a comment for a ticket
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "content", "author", "createdAt" })
public class Comment {
    @JsonProperty("content")
    private String content;

    @JsonProperty("author")
    private String author;

    @JsonProperty("createdAt")
    private String createdAt;

    /**
     * constructor for Comment class
     */
    @JsonCreator
    public Comment(@JsonProperty("content") final String text,
                   @JsonProperty("author") final String author,
                   @JsonProperty("createdAt") final String timestamp) {
        this.content = text;
        this.author = author;
        this.createdAt = timestamp;
    }
}
