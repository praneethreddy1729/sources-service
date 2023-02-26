package com.sources.service.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "Configurations")
public class SourceConfiguration {
    @Id
    private ObjectId id;
    @Indexed
    @Getter
    @Setter
    private String source_type;
    @Getter
    @Setter
    private Map<String, Object> configuration;
    public SourceConfiguration(String source_type, Map<String, Object> configuration) {
        this.source_type = source_type;
        this.configuration = configuration;
    }

}
