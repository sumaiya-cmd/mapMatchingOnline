package com.example.SpringMapMatching.Controller;

import com.example.SpringMapMatching.Model.Point;
import com.example.SpringMapMatching.Service._NewViterbiService;
import com.example.SpringMapMatching.Service._ShortestPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.example.SpringMapMatching.Service._ShortestPathService.*;

@RestController
public class ShortestPathController {
    private final _ShortestPathService shortestPathService;

    @Autowired
    public ShortestPathController(_ShortestPathService shortestPathService) {
        this.shortestPathService = shortestPathService;
    }

    @PostMapping("/findShortestDistance")
    public List<Point> findShortestDistance(@RequestBody Double[] gpsCoordinates){
        System.out.println("hello...inside shortest distance controller");
        List<Point> shortestPath = new ArrayList<>();
        if(source == null){
            source = new Point(gpsCoordinates[0], gpsCoordinates[1]);
            return shortestPath;
        }
        else if(destination == null){
            destination = new Point(gpsCoordinates[0], gpsCoordinates[1]);
            shortestPath = shortestPathService.findShortestDistance(source, destination);
            return shortestPath;
        }
        else{
            source = new Point(gpsCoordinates[0], gpsCoordinates[1]);
            System.out.println(source.getLongitude() + "," + source.getLatitude());
            System.out.println(destination.getLongitude() + "," + destination.getLatitude());
            shortestPath = shortestPathService.findShortestDistance(source, destination);
            return shortestPath;
        }
    }

    @PostMapping("/raiseAlertRoute")
    public Point raiseAlert(@RequestBody Double[] gpsCoordinates){
        System.out.println("inside raise alert......");
        Point alertPoint = findAndSetAlertPoint(gpsCoordinates);
        return alertPoint;
    }
}
