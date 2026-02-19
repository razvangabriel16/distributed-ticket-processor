package entities.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Data transfer object (DTO) representing search and filter criteria for tickets and users.
 * This class encapsulates all possible filter parameters that can be used in search operations,
 * supporting both ticket / developer filtering based on some attributes.
 * The class uses Jackson annotations for JSON serialization/deserialization and Lombok
 * for automatic getter/setter generation.
 * All fields are optional and only included in JSON when they have non null values.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Filter {
    @JsonProperty("searchType")
    private String searchType;

    @JsonProperty("businessPriority")
    private String businessPriority;

    @JsonProperty("type")
    private String type;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("createdBefore")
    private String createdBefore;

    @JsonProperty("expertiseArea")
    private String expertiseArea;

    @JsonProperty("seniority")
    private String seniority;

    @JsonProperty("createdAfter")
    private String createdAfter;

    @JsonProperty("availableForAssignment")
    private Boolean availableForAssignment;

    @JsonProperty("keywords")
    private String[] keywords;

    @JsonProperty("performanceScoreAbove")
    private Double performanceScoreAbove;

    @JsonProperty("performanceScoreBelow")
    private Double performanceScoreBelow;

    @JsonCreator
    public Filter(@JsonProperty("searchType") final String searchType,
                   @JsonProperty("businessPriority") final String businessPriority,
                   @JsonProperty("type") final String type,
                   @JsonProperty("createdAt") final String createdAt,
                  @JsonProperty("createdBefore") final String createdBefore,
                  @JsonProperty("createdAfter") final String createdAfter,
                  @JsonProperty("expertiseArea") final String expertiseArea,
                  @JsonProperty("seniority") final String seniority,
                  @JsonProperty("keywords") final String[] keywords,
                  @JsonProperty("availableForAssignment") final Boolean availableForAssignment,
                  @JsonProperty("performanceScoreAbove") final Double performanceScoreAbove,
                  @JsonProperty("performanceScoreBelow") final Double performanceScoreBelow

    ) {
        this.searchType = searchType;
        this.businessPriority = businessPriority;
        this.type = type;
        this.createdAt = createdAt;
        this.createdBefore = createdBefore;
        this.expertiseArea = expertiseArea;
        this.seniority = seniority;
        this.createdAfter = createdAfter;
        this.availableForAssignment = availableForAssignment;
        this.keywords = keywords;
        this.performanceScoreAbove = performanceScoreAbove;
        this.performanceScoreBelow = performanceScoreBelow;
    }

    /**
     * Checks if the performanceScoreAbove filter is specified.
     * @return true if performanceScoreAbove is set (non-null), false otherwise
     */
    public boolean hasPerformanceScoreAbove() {
        return performanceScoreAbove != null;
    }

    /**
     * Checks if the performanceScoreBelow filter is specified.
     * @return true if performanceScoreBelow is set (non-null), false otherwise
     */
    public boolean hasPerformanceScoreBelow() {
        return performanceScoreBelow != null;
    }

    /**
     * Checks if the searchType filter is specified.
     * @return true if searchType is set (non-null) and not empty, false otherwise
     */
    public boolean hasSearchType() {
        return searchType != null && !searchType.isEmpty();
    }

    /**
     * Checks if the businessPriority filter is specified.
     * @return true if businessPriority is set (non-null) and not empty, false otherwise
     */
    public boolean hasBusinessPriority() {
        return businessPriority != null && !businessPriority.isEmpty();
    }

    /**
     * Checks if the type filter is specified.
     * @return true if type is set (non-null) and not empty, false otherwise
     */
    public boolean hasType() {
        return type != null && !type.isEmpty();
    }

    /**
     * Checks if the createdAt filter is specified.
     * @return true if createdAt is set (non-null) and not empty, false otherwise
     */
    public boolean hasCreatedAt() {
        return createdAt != null && !createdAt.isEmpty();
    }

    /**
     * Checks if the createdBefore filter is specified.
     * @return true if createdBefore is set (non-null) and not empty, false otherwise
     */
    public boolean hasCreatedBefore() {
        return createdBefore != null && !createdBefore.isEmpty();
    }

    /**
     * Checks if the createdAfter filter is specified.
     * @return true if createdAfter is set (non-null) and not empty, false otherwise
     */
    public boolean hasCreatedAfter() {
        return createdAfter != null && !createdAfter.isEmpty();
    }

    /**
     * Checks if the expertiseArea filter is specified.
     * @return true if expertiseArea is set (non-null) and not empty, false otherwise
     */
    public boolean hasExpertiseArea() {
        return expertiseArea != null && !expertiseArea.isEmpty();
    }

    /**
     * Checks if the seniority filter is specified.
     * @return true if seniority is set (non-null) and not empty, false otherwise
     */
    public boolean hasSeniority() {
        return seniority != null && !seniority.isEmpty();
    }

    /**
     * Checks if the keywords filter is specified.
     * @return true if keywords is set (non-null) and positive length, false otherwise
     */
    public boolean hasKeywords() {
        return keywords != null && keywords.length > 0;
    }

    /**
     * Checks if the availableForAssignment filter is specified.
     * @return true if availableForAssignment is set (non-null), false otherwise
     */
    public boolean hasAvailableForAssignment() {
        return availableForAssignment != null;
    }

    /**
     * Converts the keywords array to a List for easier processing.
     *
     * @return a List containing all keywords, or an empty List if no keywords are specified
     */
    public List<String> getKeywordsList() {
        return keywords != null ? Arrays.asList(keywords) : List.of();
    }

}
