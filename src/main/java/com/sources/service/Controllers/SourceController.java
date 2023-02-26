package com.sources.service.Controllers;

import com.sources.service.models.SourceTemplate;
import com.sources.service.utils.mongoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("v1/source")
public class SourceController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @RequestMapping("template/{source_type}")
    @GetMapping
    public ResponseEntity<SourceTemplate> getSourceTemplate(@PathVariable("source_type") String sourceType) {
        SourceTemplate template = new mongoUtils(mongoTemplate).getSourceTemplate(sourceType);
        if (template == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(template);
    }

    @RequestMapping("template")
    @PostMapping
    public ResponseEntity<Object> addSourceTemplate(@RequestBody Map<String, Object> requestBody) {
        boolean successful = new mongoUtils(mongoTemplate).addSourceTemplate(requestBody);
        if (successful) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid template format");
    }

    @RequestMapping("{source_type}")
    @PostMapping
    public ResponseEntity<Object> addSource(@PathVariable("source_type") String sourceType, @RequestBody Map<String, Object> requestBody) {
        String response = new mongoUtils(mongoTemplate).validateAndAddSourceConfiguration(sourceType, requestBody);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
