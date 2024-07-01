package com.example.SpringMapMatching.Controller;

import com.example.SpringMapMatching.Model.Point;
import com.example.SpringMapMatching.Service._ApplyViterbiService;
import com.example.SpringMapMatching.Service._NewViterbiService;
import com.example.SpringMapMatching.Service._ShortestPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class ViterbiController {

    public static List<Double[]> observations = new ArrayList<>();

    private final _NewViterbiService viterbiService;
    public static int prev = 0;
    public static int curr = 0;
//    public static List<List<Point>> result = new ArrayList<>();
    public static List<Point> result = new ArrayList<>();
    public static List<Double[]> input = new ArrayList<>();


    @Autowired
    public ViterbiController(_NewViterbiService viterbiService) {
        this.viterbiService = viterbiService;
    }

    @PostMapping("/processCoordinates")
    public List<Point> processCoordinates(@RequestBody Double[] gpsCoordinates) {
        //observations = gpsCoordinates;
        System.out.println("inside controller : " + gpsCoordinates[0] + ", " + gpsCoordinates[1]);
        input.add(gpsCoordinates);
        if(_NewViterbiService.isBeginning){
            observations.add(gpsCoordinates);
            if(observations.size() == 5){
               result.addAll(viterbiService.process(observations));
                if(!_NewViterbiService.checkIntersectionInsideWindow)
                {
                    observations.clear();
                }
                else{
                    observations.subList(0, _NewViterbiService.makeWindow).clear();
                }
               return result;
            }
            else{
                return result;
            }
        }
        else{
            if(_NewViterbiService.isIntersection){
                //System.out.println("new window formation from intersection");
                observations.add(gpsCoordinates);
                //System.out.println("--" + observations.size() + " " +_NewViterbiService.extendedWindow);

                if(observations.size() == _NewViterbiService.extendedWindow){
                    List<Point> newResult = viterbiService.process(observations);

                    if(newResult != null){
                        result.addAll(newResult);

                        if(!_NewViterbiService.checkIntersectionInsideWindow)
                        {
                            observations.clear();
                        }
                        else{
                            observations.subList(0, _NewViterbiService.makeWindow).clear();
                            //System.out.println("----------" + observations.size());
                            //System.out.println(_NewViterbiService.isIntersection);
                        }
                        return result;
                    }
                    else{
                        return result;
                    }
                }
                else{
                    return result;
                }
            }
            else{
                observations.add(gpsCoordinates);
                List<Point> newResult = viterbiService.process(observations);
                if(newResult != null){
                    if(newResult.get(0).getLongitude().equals(-1.0) && newResult.get(0).getLatitude().equals(-1.0)){
                        //observations.clear();
                        System.out.println("size of observations : " + observations.size());
                        return result;
                    }
                    result.addAll(newResult);
                    if(_NewViterbiService.uturn){
                        System.out.println("size of observations : " + observations.size());
                        _NewViterbiService.uturn = false;
                    }
                    else{
                        observations.clear();
                    }
                    return result;
                }
                else{
                    //observations.add(gpsCoordinates);
                    return result;
                }
            }
        }
    }

    @GetMapping("/processCoordinates")
    public List<List<Point>> processCoordinatesAndSend() {
        List<Point> result1 =  viterbiService.process(observations);
        _ApplyViterbiService applyViterbiService = new _ApplyViterbiService();
        List<Point> result = applyViterbiService.applyViterbi(observations);
        List<Point> obs = new ArrayList<>();

        for(int i = 0; i < observations.size(); i++){
            Point p = new Point(observations.get(i)[0], observations.get(i)[1]);
            obs.add(p);
        }
        List<List<Point>> finalResult = new ArrayList<>();
        finalResult.add(obs);
        finalResult.add(result1);
        return finalResult;
    }

    @PostMapping("/clearData")
    public ResponseEntity<Map<String, String>> clearData(@RequestBody Map<String, String> requestBody) {
        Map<String, String> response = new HashMap<>();
        System.out.println("data cleared ------------------------");
        for(Double[] input : input){
            System.out.println("input.add(new Double[]{" + input[0] + "," + input[1] + "})");
        }
//        for(Point result : result){
//            System.out.println("expected.add(new Double[]{" + result.getLongitude() + "," + result.getLatitude() + "})");
//        }
        if (requestBody.containsKey("data") && "clear".equals(requestBody.get("data"))) {
            // Logic to clear data
            _NewViterbiService.isIntersection = false;
            _NewViterbiService.isBeginning = true;
            _NewViterbiService.most_likely_path = -1;
            result.clear();
            _NewViterbiService.checkWindowExtention = false;
            _NewViterbiService.checkIntersectionInsideWindow = false;
            _NewViterbiService.extendedWindow = 5;
            observations.clear();
            _NewViterbiService.distanceFromPoint.clear();
            _NewViterbiService.nearestPointsFromSegments.clear();
            _NewViterbiService.uturn = false;
            _NewViterbiService.visited.clear();
            _NewViterbiService.prev_MLP = -1;
            _NewViterbiService.countOfVisited = 0;
            _NewViterbiService.leftPath.clear();
            _ShortestPathService.destination = null;
            _ShortestPathService.source = null;
            _ShortestPathService.restrictedSegment = -1;
            _ShortestPathService.restrictedPoints.clear();
            _ShortestPathService.alertPoints.clear();
            _ShortestPathService.isFirstSource = true;
            _ShortestPathService.currentSegmentId = -1;
            response.put("message", "Data cleared successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid payload");
            return ResponseEntity.badRequest().body(response);
        }
    }

}


//                if(observations.size() == 8){
//
//                    result.addAll(viterbiService.process(observations));
//                    if(!_NewViterbiService.checkIntersectionInsideWindow)
//                    {
//                        observations.clear();
//                    }
//                    else{
//                        observations.subList(0, _NewViterbiService.makeWindow).clear();
//                    }
//                    return result;
//                }
//                else{
//                    return result;
//                }

//    @PostMapping("/api/map-matching")
//    public String mapMatching(@RequestBody CoordinatesDto coordinates) {
//        double latitude = coordinates.getLatitude();
//        double longitude = coordinates.getLongitude();
//
//        // Perform map matching or any processing with the coordinates
//        // For example, you can call a map matching service or algorithm here
//
//        // Return the mapped coordinates as a response (for demonstration, just echoing back)
//        System.out.println(latitude+" "+longitude);
//        Double[] obs = {longitude, latitude};
//        observations.add(obs);
//        return "Mapped Coordinates: Latitude " + latitude + ", Longitude " + longitude;
//    }