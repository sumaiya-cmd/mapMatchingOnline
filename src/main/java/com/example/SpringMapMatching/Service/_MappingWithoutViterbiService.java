package com.example.SpringMapMatching.Service;

import com.example.SpringMapMatching.Database.Data;
import com.example.SpringMapMatching.Database.LocationNavPath;
import com.example.SpringMapMatching.Model.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class _MappingWithoutViterbiService {


    private Data data; // Autowire your Data repository

    @Autowired
    public _MappingWithoutViterbiService(Data data){
        this.data = data;
    }

    @Autowired
    private RoadGraph roadGraph;

    @Autowired
    private SegmentGraph segmentGraph;
//    @Autowired
//    public static SegmentGraph segmentGraph;

    //public static int most_likely_path = -1;

    public Point mapping(List<Double> currObs, Map<Integer, List<List<Double>>> segmentMapping) {
        List<LocationNavPath> nearestSegment = data.findByLocationNear(currObs.get(0), currObs.get(1), 100); // Adjust max distance as needed
        Point mappedPoint = null;

        int currNearestSegmentID = nearestSegment.get(0).getH_ID();

        if(currNearestSegmentID == _NewViterbiService.most_likely_path){
            List<List<Double>> segmentData = segmentMapping.get(_NewViterbiService.most_likely_path);
            Point nearestPointOnMLP = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), segmentData);
            return nearestPointOnMLP;
        }
        else{
            boolean mostLikelyPathFound = false;

            for (LocationNavPath segment : nearestSegment){
                currNearestSegmentID = segment.getH_ID();
                if(currNearestSegmentID == _NewViterbiService.most_likely_path){
                    mostLikelyPathFound = true;
                }
            }

            if(mostLikelyPathFound){
                List<List<Double>> segmentData = segmentMapping.get(_NewViterbiService.most_likely_path);
                Point nearestPointOnMLP = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), segmentData);
                boolean isIntersectionPoint = isIntersectionPoint(nearestPointOnMLP);

                if(isIntersectionPoint){
                    for(LocationNavPath segment : nearestSegment){
                        currNearestSegmentID = segment.getH_ID();
                        boolean isConnected = _ConnectionOrIntersectionService.connectionExist(_NewViterbiService.most_likely_path, currNearestSegmentID, segmentGraph);
                        if(isConnected){
                            List<List<Double>> currSegmentData = segmentMapping.get(_NewViterbiService.most_likely_path);
                            Point nearestPointOnCurrSegment = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), currSegmentData);
                            _NewViterbiService.nearestPointsFromSegments.add(nearestPointOnCurrSegment);
                            _NewViterbiService.distanceFromPoint.add(Point.haversineDistance(nearestPointOnCurrSegment, nearestPointOnMLP));
                            if(_NewViterbiService.distanceFromPoint.size() >= 2){
                                double prev = _NewViterbiService.distanceFromPoint.get(_NewViterbiService.distanceFromPoint.size() - 2);
                                double curr = _NewViterbiService.distanceFromPoint.get(_NewViterbiService.distanceFromPoint.size() - 1);
                                if(curr > prev){
                                    _NewViterbiService.distanceFromPoint.clear();
                                    _NewViterbiService.nearestPointsFromSegments.clear();
                                    return null;
                                }
                                else{
                                    System.out.println("here");
                                    return nearestPointOnMLP;
                                }
                            }
                            else{
                                _NewViterbiService.distanceFromPoint.clear();
                                _NewViterbiService.nearestPointsFromSegments.clear();
                                return null;
                            }
                        }
                    }
                }
                else{
                    return nearestPointOnMLP;
                }
            }
            return directMapping(currObs, nearestSegment, segmentMapping);
        }
    }

    private static Point findNearestPointFromRoad(Point p, List<List<Double>> roadSegment) {
        Point result = null;
        Double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < roadSegment.size(); i++) {
            Point ptOnRoad = new Point(roadSegment.get(i).get(0), roadSegment.get(i).get(1));
            double distance = Point.haversineDistance(p, ptOnRoad);
            if (distance < minDistance) {
                minDistance = distance;
                result = ptOnRoad;
            }
        }
        return result; 
    }

    private Point directMapping(List<Double> currObs, List<LocationNavPath> nearestSegment, Map<Integer, List<List<Double>>> segmentMapping){
        Point mappedPoint = null;
        int prev_mls = _NewViterbiService.most_likely_path;

        for (LocationNavPath segment : nearestSegment) {
            int cns_id = segment.getH_ID();
            System.out.println(cns_id + " " + "--mls : " + _NewViterbiService.most_likely_path);

            if (cns_id == _NewViterbiService.most_likely_path) {

                List<List<Double>> segmentData = segmentMapping.get(cns_id);
                mappedPoint = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), segmentData);
                return mappedPoint;
            } else {
                boolean isConnected = _ConnectionOrIntersectionService.connectionExist(_NewViterbiService.most_likely_path, cns_id, segmentGraph);

                if (isConnected) {
                    boolean isIntersection = _ConnectionOrIntersectionService.intersectionExist(_NewViterbiService.most_likely_path, cns_id, roadGraph);
                    //System.out.println("A " + isIntersection +  " " +  cns_id + " " + "mls : " + _NewViterbiService.most_likely_path);
                    if (!isIntersection) {
                        _NewViterbiService.most_likely_path = cns_id;
                        List<List<Double>> segmentData = segmentMapping.get(cns_id);
                        mappedPoint = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), segmentData);
                        //System.out.println("xyz");
                        return mappedPoint;
                    }
                    else{
                        return null;
                    }
                }
            }
        }
        List<List<Double>> segmentData = segmentMapping.get(prev_mls);
        mappedPoint = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), segmentData);
        return mappedPoint;
    }

    private boolean isIntersectionPoint(Point pt){
        if (roadGraph.getRoadGraph().degreeOf(pt.getLongitude() + "#" + pt.getLatitude()) >= 3) {
            return true;
        }
        return false;
    }
}

