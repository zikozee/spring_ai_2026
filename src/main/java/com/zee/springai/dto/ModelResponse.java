package com.zee.springai.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@NoArgsConstructor
@Data
public class ModelResponse {

    private String object;
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String id;
        private String object;
        private Integer created;
        @JsonProperty("owned_by")
        private String ownedBy;
    }
}
