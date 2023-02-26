package com.sources.service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "Templates")
public class SourceTemplate {
    @Id
    private ObjectId id;
    @Indexed
    @Getter
    @Setter
    private String source_type;

    @Getter
    @Setter
    private Map<String, Object> template;

    public SourceTemplate(String source_type, Map<String, Object> template) {
        this.source_type = source_type;
        this.template = template;
    }
}
