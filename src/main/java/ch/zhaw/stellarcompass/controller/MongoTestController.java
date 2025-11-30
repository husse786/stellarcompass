package ch.zhaw.stellarcompass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

@RestController
public class MongoTestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/api/testmongodb")
    public ResponseEntity<String> testMongoDb() {
        try {
            // Test-Daten schreiben
            Long time = System.currentTimeMillis();
            DBObject objectToSave = BasicDBObjectBuilder.start().add("time", time).get();
            DBObject saved = mongoTemplate.save(objectToSave, "TestCollection");

            // Test-Daten lesen
            DBObject read = mongoTemplate.findById(saved.get("_id"), DBObject.class, "TestCollection");

            if (read != null) {
                return new ResponseEntity<>("Connection OK! Saved ID: " + saved.get("_id"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error: Saved but not found.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Connection Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}