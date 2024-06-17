package com.example.SpringMapMatching.Service;

import com.example.SpringMapMatching.Database.Data;
import com.example.SpringMapMatching.Database.LocationNavPath;
import com.example.SpringMapMatching.Model.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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


    public Point mapping(List<Double> currObs, Map<Integer, List<List<Double>>> segmentMapping) {
        List<LocationNavPath> nearestSegment = data.findByLocationNear(currObs.get(0), currObs.get(1), 20); // Adjust max distance as needed
        Point mappedPoint = null;

        int currNearestSegmentID = nearestSegment.get(0).getH_ID();

        if(currNearestSegmentID == _NewViterbiService.most_likely_path){
            //System.out.println("xx");
            List<List<Double>> segmentData = segmentMapping.get(_NewViterbiService.most_likely_path);
            Point nearestPointOnMLP = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), segmentData);
            _NewViterbiService.visited.addAll(_CalculateResultService.visitedPoints(nearestPointOnMLP));
            //_NewViterbiService.lastInVisited = nearestPointOnMLP;
            return nearestPointOnMLP;
        }
        else{
            //System.out.println("yy");
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
                //System.out.println("most likely path found....");

                if(isIntersectionPoint){
                    //System.out.println("Intersection Point........");
                    for(LocationNavPath segment : nearestSegment){
                        currNearestSegmentID = segment.getH_ID();
                        boolean isConnected = _ConnectionOrIntersectionService.connectionExist(_NewViterbiService.most_likely_path, currNearestSegmentID, segmentGraph);
                        //System.out.println("most likely path : " + _NewViterbiService.most_likely_path);
                        if(isConnected){
                            List<List<Double>> currSegmentData = segmentMapping.get(currNearestSegmentID);
                            Point nearestPointOnCurrSegment = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), currSegmentData);
                            _NewViterbiService.nearestPointsFromSegments.add(nearestPointOnCurrSegment);
                            _NewViterbiService.distanceFromPoint.add(Point.haversineDistance(nearestPointOnCurrSegment, nearestPointOnMLP));
                            if(_NewViterbiService.distanceFromPoint.size() >= 2){
                                double prev = _NewViterbiService.distanceFromPoint.get(_NewViterbiService.distanceFromPoint.size() - 2);
                                double curr = _NewViterbiService.distanceFromPoint.get(_NewViterbiService.distanceFromPoint.size() - 1);
                                //System.out.println("prev : " + prev + " curr : " + curr);
                                if(curr > prev){
                                    _NewViterbiService.distanceFromPoint.clear();
                                    _NewViterbiService.nearestPointsFromSegments.clear();
                                    return null;
                                }
                                else{
                                    //System.out.println("here");
                                    _NewViterbiService.visited.addAll(_CalculateResultService.visitedPoints(nearestPointOnMLP));
                                    //_NewViterbiService.lastInVisited = nearestPointOnMLP;
                                    return nearestPointOnMLP;
                                }
                            }
                        }
                    }
                }
                else{
                    if(_NewViterbiService.visited.contains(nearestPointOnMLP.getLongitude() + "#" + nearestPointOnMLP.getLatitude())){
                        System.out.println("---------" + nearestPointOnMLP.getLongitude() + "," + nearestPointOnMLP.getLatitude());
                        if(nearestPointOnMLP.getLongitude().equals(_NewViterbiService.lastInVisited.getLongitude()) && nearestPointOnMLP.getLatitude().equals(_NewViterbiService.lastInVisited.getLatitude())){
                            //_NewViterbiService.visited.addAll(_CalculateResultService.visitedPoints(nearestPointOnMLP));
                            //_NewViterbiService.lastInVisited = nearestPointOnMLP;
                            return nearestPointOnMLP;
                        }
                        System.out.println(nearestPointOnMLP.getLongitude() + "," + nearestPointOnMLP.getLatitude());
                        System.out.println("visited contains the incoming point......");
                        _NewViterbiService.countOfVisited++;
                        if(_NewViterbiService.countOfVisited >= 3){
                            _NewViterbiService.uturn = true;
                            _NewViterbiService.leftPath.addAll(findingLeftPath(_NewViterbiService.firstInVisited, _NewViterbiService.lastInVisited, _NewViterbiService.secondlastInVisited, _NewViterbiService.visited));
                            return null;
                        }
                        else{
                            Point nullPt = new Point(-1.0, -1.0);
                            return nullPt;
                        }
                    }
                    _NewViterbiService.visited.addAll(_CalculateResultService.visitedPoints(nearestPointOnMLP));
                    //_NewViterbiService.lastInVisited = nearestPointOnMLP;
                    _NewViterbiService.countOfVisited = 0;
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
            //System.out.println(cns_id + " " + "--mls : " + _NewViterbiService.most_likely_path);

            if (cns_id == _NewViterbiService.most_likely_path) {
                List<List<Double>> segmentData = segmentMapping.get(cns_id);
                mappedPoint = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), segmentData);
                _NewViterbiService.visited.addAll(_CalculateResultService.visitedPoints(mappedPoint));
                //_NewViterbiService.lastInVisited = mappedPoint;
                return mappedPoint;
            } else {
                boolean isConnected = _ConnectionOrIntersectionService.connectionExist(_NewViterbiService.most_likely_path, cns_id, segmentGraph);

                if (isConnected) {
                    boolean isIntersection = _ConnectionOrIntersectionService.intersectionExist(_NewViterbiService.most_likely_path, cns_id, roadGraph);
                    if (!isIntersection) {
                        _NewViterbiService.prev_MLP = _NewViterbiService.most_likely_path;
                        _NewViterbiService.most_likely_path = cns_id;
                        List<List<Double>> segmentData = segmentMapping.get(cns_id);
                        mappedPoint = findNearestPointFromRoad(new Point(currObs.get(0), currObs.get(1)), segmentData);
                        _NewViterbiService.visited.clear();
                        Point connectionPoint = _CalculateResultService.connectionPoint(_NewViterbiService.prev_MLP, _NewViterbiService.most_likely_path, _NewViterbiService.segmentMapping);
                        _NewViterbiService.visited.addAll(_CalculateResultService.visitedPoints(connectionPoint, mappedPoint, _NewViterbiService.most_likely_path, _NewViterbiService.segmentMapping));
                        _NewViterbiService.lastInVisited = mappedPoint;
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
        _NewViterbiService.visited.addAll(_CalculateResultService.visitedPoints(mappedPoint));
        //_NewViterbiService.lastInVisited = mappedPoint;
        return mappedPoint;
    }

    private boolean isIntersectionPoint(Point pt){
        if (roadGraph.getRoadGraph().degreeOf(pt.getLongitude() + "#" + pt.getLatitude()) >= 3) {
            return true;
        }
        return false;
    }

    private List<Point> findingLeftPath(Point firstInVisited, Point lastInVisited, Point secondLastInVisited, Set<String> visited){
        List<List<Double>> segment = _NewViterbiService.segmentMapping.get(_NewViterbiService.most_likely_path);
        List<Double> startPoint = segment.get(0);
        List<Double> endPoint = segment.get(segment.size() - 1);
        List<Point> leftPath = new ArrayList<>();

        int indexOfLastInVisited = -1;
        //int indexOfSecondLastInVisited = -1;
        for(int i = 0; i < segment.size(); i++){
            if(segment.get(i).get(0).equals(lastInVisited.getLongitude()) && segment.get(i).get(1).equals(lastInVisited.getLatitude())){
                indexOfLastInVisited = i;
            }
        }

        if((firstInVisited.getLongitude().equals(startPoint.get(0))) && (firstInVisited.getLatitude().equals(startPoint.get(1)))){
            for(int i = indexOfLastInVisited; i < segment.size(); i++){
                leftPath.add(new Point(segment.get(i).get(0), segment.get(i).get(1)));
            }
        }
        else if((firstInVisited.getLongitude().equals(endPoint.get(0))) && (firstInVisited.getLatitude().equals(endPoint.get(1)))){
            for(int i = indexOfLastInVisited; i >= 0; i--){
                leftPath.add(new Point(segment.get(i).get(0), segment.get(i).get(1)));
            }
        }
        else{
            String next = segment.get(indexOfLastInVisited + 1).get(0) + "#" + segment.get(indexOfLastInVisited + 1).get(1);
            if(visited.contains(next)){
                for(int i = indexOfLastInVisited; i >= 0; i--){
                    leftPath.add(new Point(segment.get(i).get(0), segment.get(i).get(1)));
                }
            }
            else{
                for(int i = indexOfLastInVisited; i < segment.size(); i++){
                    leftPath.add(new Point(segment.get(i).get(0), segment.get(i).get(1)));
                }
            }
        }
        //Collections.reverse(leftPath);
        return leftPath;
    }
}

