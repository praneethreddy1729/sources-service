package com.sources.service.utils;

import com.sources.service.models.SourceConfiguration;
import com.sources.service.models.SourceTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mongoUtils {
    private final MongoTemplate mongoTemplate;

    public mongoUtils(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public SourceTemplate getSourceTemplate(String sourceType) {
        List<SourceTemplate> list = mongoTemplate.find(Query.query(Criteria.where("source_type").is(sourceType)), SourceTemplate.class);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public boolean addSourceTemplate(Map<String, Object> requestBody) {
        if (requestBody == null || requestBody.size() != 2 || requestBody.get("type") == null || requestBody.get("fields") == null) {
            return false;
        }
        SourceTemplate sourceTemplate = new SourceTemplate(requestBody.get("type").toString(), (Map<String, Object>) requestBody.get("fields"));
        mongoTemplate.insert(sourceTemplate);
        return true;
    }

    public String validateAndAddSourceConfiguration(String sourceType, Map<String, Object> requestBody) {
        SourceTemplate sourceTemplate = this.getSourceTemplate(sourceType);
        if (sourceTemplate == null) {
            return "sourceType not found";
        }
        Map<String, Object> template = sourceTemplate.getTemplate();
        Set<String> fields = template.keySet();
        for (Map.Entry<String, Object> entry: requestBody.entrySet()) {
            String field = entry.getKey();
            Map<String, Object> fieldDetails = (Map<String, Object>) template.get(field);
            if (fieldDetails == null) {
                return String.format("Error: Unknown field: %s", field);
            }
            fields.remove(field);
            Object value = entry.getValue();
            if (value != null) {
                value = value.toString();
            }
            if (fieldDetails.get("required") != null && (boolean)fieldDetails.get("required") == true && (value == null || value.toString().isEmpty())) {
                return String.format("Error: %s is required.", field);
            }
            if (fieldDetails.get("regex") != null && value != null) {
                String regex = fieldDetails.get("regex").toString();
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(value.toString());
                if (!matcher.matches()) {
                    return fieldDetails.get("regexErrorMessage").toString();
                }
            }
            if (fieldDetails.get("options") != null && value != null) {
                List<Map<String, String>> optionsList = (List<Map<String, String>>) fieldDetails.get("options");
                value = value.toString();
                boolean exists = false;
                for (Map<String, String> option : optionsList) {
                    if (value.equals(option.get("value"))) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    return String.format("Error: Please select from given options for %s", field);
                }
            }
        }
        if (fields.size() != 0) {
            for (String field : fields) {
                Map<String, Object> fieldDetails = (Map<String, Object>) template.get(field);
                if (fieldDetails.get("required") != null && (boolean)fieldDetails.get("required") == true) {
                    return "Required fields are missing";
                }
            }
        }
        mongoTemplate.insert(new SourceConfiguration(sourceType, requestBody));
        return null;
    }
}
