package com.example.SpringMapMatching.Database;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.Document;
import java.util.List;

@Repository
public interface Data extends MongoRepository<LocationNavPath,String> {
    @Query(value = "{'geometry': { $geoNear : { $geometry : { type : 'Point', coordinates : [?0, ?1] }, $maxDistance: ?2 } } }")
    List<LocationNavPath> findByLocationNear(double longitude, double latitude, double maxDistance);

    @Query(value = "{'H_ID': ?0}")
    LocationNavPath findByH_ID(Integer H_ID);

    @Query(value = "{" +
            "'$geoNear': {" +
            "   'near': {" +
            "       'type': 'Point'," +
            "       'coordinates': [?0, ?1]" +
            "   }," +
            "   'distanceField': 'distance'," +
            "   'maxDistance': ?2," +
            "   'spherical': true" +
            "}" +
            "}, " +
            "{" +
            "'$unwind': {" +
            "   'path': '$geometry.coordinates'," +
            "   'includeArrayIndex': 'index'," +
            "   'preserveNullAndEmptyArrays': true" +
            "}" +
            "}, " +
            "{" +
            "'$set': {" +
            "   'geometry.type': 'Point'" +
            "}" +
            "}"
    )
    List<Document> findAndSplitNearbyRoads(double coordinate1, double coordinate2, double maxDistance);

    @Query(value = "{" +
            "'$geoNear': {" +
            "   'near': { 'type': 'Point', 'coordinates': [?0, ?1] }," +
            "   'distanceField': 'string'," +
            "   'maxDistance': ?2," +
            "   'query': {}," +
            "   'includeLocs': ''," +
            "   'num': number," +  // Specify the number of documents to return
            "   'spherical': boolean" +
            "}" +
            "}")
    List<LocationNavPath> findNearestPointsOnRoadFromObservation(double nearCoordinate1, double nearCoordinate2, double maxDistance);

}