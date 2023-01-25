package com.example.DataCache;

import com.example.demo.PropertiesCacheService;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class RestServiceDataCache {
    @Autowired
    private PropertiesCacheService propertiesCacheService;

    @Autowired
    private MongoClient mongoClient;

    public boolean checkConnection() {
        try {
            return mongoClient.listDatabaseNames().first() != null;
        } catch (MongoException e) {
            return false;
        }
    }

    public Document findEntryByUsername(String collectionName, String username) {
        try {
            MongoDatabase db = mongoClient.getDatabase(propertiesCacheService.getProperty("spring.data.mongodb.database"));
            MongoCollection<Document> collection = db.getCollection(collectionName);
            Bson filter = Filters.eq("username", username);
            return collection.find(filter).first();
        } catch (MongoException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean entryExistsWithUsername(String collectionName, String username) {
        try {
            MongoDatabase db = mongoClient.getDatabase(propertiesCacheService.getProperty("spring.data.mongodb.database"));
            MongoCollection<Document> collection = db.getCollection(collectionName);
            Bson filter = Filters.eq("username", username);
            return collection.countDocuments(filter) > 0;
        } catch (MongoException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void addToCollection(String collectionName, String jsonDoc) {
        try {
            MongoDatabase db = mongoClient.getDatabase(propertiesCacheService.getProperty("spring.data.mongodb.database"));
            MongoCollection<Document> collection = db.getCollection(collectionName);
            Document doc = Document.parse(jsonDoc);
            collection.insertOne(doc);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public String getCollection(String col){
        MongoDatabase db = mongoClient.getDatabase(propertiesCacheService.getProperty("spring.data.mongodb.database"));
        MongoCollection<Document> collection = db.getCollection(col);

        FindIterable<Document> iterable = collection.find();

//        Bson filter = Filters.eq("name", "Nik");
//        FindIterable<Document> itrbl = collection.find(filter);
//        Bson fltr = Filters.eq("name", "Nik");
//        Document document = collection.find(fltr).first();

        MongoCursor<Document> cursor = iterable.iterator();

        String jsonObject = "[";

        while(cursor.hasNext()) {
            jsonObject += cursor.next().toJson();
            if(cursor.hasNext())
                jsonObject += ", ";
        }

        jsonObject += "]";

        return jsonObject;
    }

    public Map<String, Document> userCookies = new HashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // This is probably a bad idea cuz its storing the whole Document and not replacing existing ones
    // yolo
    public void cookiePut(String key, Document value) {
        userCookies.put(key, value);
        scheduler.schedule(() -> userCookies.remove(key), 1, TimeUnit.HOURS);
    }

    public Document cookieGet(String key) {
        return userCookies.get(key);
    }
}