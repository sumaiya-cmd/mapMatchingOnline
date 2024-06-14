package com.example.SpringMapMatching.Service;

import com.example.SpringMapMatching.Controller.ViterbiController;
import com.example.SpringMapMatching.Model.Point;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class _CalculateResultService {
    static Map<Integer, Integer> stateToSegmentID = new HashMap<>();

    @Autowired
    private SegmentGraph segmentGraph;

    @Autowired
    private RoadGraph roadGraph;


    private static int num = 0;
    private static double gpsErrorInitial;

    public List<Point> calculateResult(double[][] viterbi, int[][] backpointer, Map<Integer, Integer> segmentToState, Point[][] closestPoints, List<Double[]> gpsCoordinates) {
        //System.out.println(gpsCoordinates.size());
        //System.out.println(gpsCoordinates.get(0)[0] + " " + gpsCoordinates.get(0)[1]);
//        for(int k = 0; k < viterbi.length; k++){
//            for(int l = 0; l < viterbi[k].length; l++){
//                System.out.print(viterbi[k][l] + " ");
//            }
//            System.out.println("");
//        }
//        System.out.println(viterbi.length);
//
//        for(int k = 0; k < stateToSegmentID.size(); k++){
//            System.out.print(stateToSegmentID + " ");
//        }
//
//        for(int k = 0; k < viterbi.length; k++){
//            for(int l = 0; l < viterbi[k].length; l++){
//                System.out.print(stateToSegmentID.get(backpointer[k][l]) + " ");
//            }
//            System.out.println("");
//        }
        double maxProb = Double.NEGATIVE_INFINITY;
        int maxState = -1;

        for (int j = 0; j < segmentToState.size(); j++) {
            if (viterbi[viterbi.length - 1][j] != 0.0 && viterbi[viterbi.length - 1][j] > maxProb) {
                maxProb = viterbi[viterbi.length - 1][j];
                maxState = j;
            }
        }


        int[] path = new int[gpsCoordinates.size()];
        path[gpsCoordinates.size() - 1] = maxState;


        for (int t = gpsCoordinates.size() - 2; t >= 0; t--) {
            path[t] = backpointer[t + 1][path[t + 1]];
        }

//        for(int t = 0; t < path.length; t++){
//            System.out.print(stateToSegmentID.get(path[t]) + " ");
//        }
//        System.out.println("------------------------");

        for (int t = 1; t < gpsCoordinates.size(); t++) {
            if (stateToSegmentID.get(path[t - 1]) != stateToSegmentID.get(path[t])) {
                boolean isWindowBreak = isIntersectionWindow(stateToSegmentID.get(path[t - 1]), stateToSegmentID.get(path[t]));
                if (isWindowBreak) {
                    //System.out.println("window breaking");
                    //System.out.println(stateToSegmentID.get(path[t - 1]) + " " + stateToSegmentID.get(path[t]));
                    //System.out.println(gpsCoordinates.get(t)[0] + "," + gpsCoordinates.get(t)[1]);
                    //System.out.println(gpsCoordinates.get(t - 1)[0] + "," + gpsCoordinates.get(t - 1)[1]);

                    maxProb = Double.NEGATIVE_INFINITY;
                    maxState = -1;

                    for (int k = 0; k < segmentToState.size(); k++) {
                        if (viterbi[t - 1][k] != 0.0 && viterbi[t - 1][k] > maxProb) {
                            maxProb = viterbi[t - 1][k];
                            maxState = k;
                        }
                    }

                    //_NewViterbiService.most_likely_path = stateToSegmentID.get(maxState);
                    int[] new_path = new int[t];
                    new_path[t - 1] = maxState;

                    for (int x = t - 2; x >= 0; x--) {
                        new_path[x] = backpointer[x + 1][new_path[x + 1]];
                    }

                    List<Point> result = new ArrayList<>();
                    double error = 0.0;
                    for (int x = 0; x < t; x++) {
                        result.add(closestPoints[x][new_path[x]]);
                        if (_NewViterbiService.isBeginning) {
                            error += Point.haversineDistance(new Point(gpsCoordinates.get(x)[0], gpsCoordinates.get(x)[1]), closestPoints[x][new_path[x]]);
                        }
                    }

                    if (_NewViterbiService.isBeginning) {
                        gpsErrorInitial = error / (double) (t);
                    }
                    _NewViterbiService.makeWindow = t;
                    _NewViterbiService.checkIntersectionInsideWindow = true;
                    _NewViterbiService.extendedWindow = 5;
                    _NewViterbiService.prev_MLP = _NewViterbiService.most_likely_path;
                    _NewViterbiService.most_likely_path = stateToSegmentID.get(maxState);
                    _NewViterbiService.visited.clear();
                    if (_NewViterbiService.prev_MLP != -1) {
                        Point connectionPoint = connectionPoint(_NewViterbiService.prev_MLP, _NewViterbiService.most_likely_path, _NewViterbiService.segmentMapping);
                        _NewViterbiService.visited.addAll(visitedPoints(connectionPoint, result.get(result.size() - 1), _NewViterbiService.most_likely_path, _NewViterbiService.segmentMapping));
                    } else {
                        _NewViterbiService.visited.addAll(visitedPoints(result));
                    }
                    return result;
                } else {
                    continue;
                }
            }
        }

        List<Point> result = new ArrayList<>();

        double error = 0.0;

        List<Double> gpsErrorOfPaths = computeGPSError(backpointer, maxState, segmentToState, closestPoints, gpsCoordinates);

        double minError = Double.POSITIVE_INFINITY;
        int minErrorState = -1;

        if (_NewViterbiService.isBeginning) {
            for (int t = 0; t < path.length; t++) {
                result.add(closestPoints[t][path[t]]);
                error += Point.haversineDistance(new Point(gpsCoordinates.get(t)[0], gpsCoordinates.get(t)[1]), closestPoints[t][path[t]]);
            }
            ///System.out.println("result : ");
//            for(int x = 0; x < result.size(); x++){
//                System.out.println(result.get(x).getLongitude() + " " + result.get(x).getLatitude());
//            }
           // System.out.println(result.size());
            _NewViterbiService.prev_MLP = _NewViterbiService.most_likely_path;
            _NewViterbiService.most_likely_path = stateToSegmentID.get(maxState);
            _NewViterbiService.visited.clear();
            if (_NewViterbiService.prev_MLP != -1) {
                //System.out.println("a");
                //System.out.println(result.get(result.size() - 1));
                Point connectionPoint = connectionPoint(_NewViterbiService.prev_MLP, _NewViterbiService.most_likely_path, _NewViterbiService.segmentMapping);
                _NewViterbiService.visited.addAll(visitedPoints(connectionPoint, result.get(result.size() - 1), _NewViterbiService.most_likely_path, _NewViterbiService.segmentMapping));
            } else {
                //System.out.println("b");
                //System.out.println(result.get(result.size() - 1));
                _NewViterbiService.visited.addAll(visitedPoints(result));
            }
            gpsErrorInitial = error / (5.00);
            _NewViterbiService.checkWindowExtention = false;
            return result;
        } else {
            for (int k = 0; k < gpsErrorOfPaths.size(); k++) {
                double absGPSError = Math.abs(gpsErrorInitial - gpsErrorOfPaths.get(k));
                //double absGPSError = gpsErrorOfPaths.get(k);
                if (absGPSError < minError) {
                    minError = absGPSError;
                    minErrorState = k;
                }
            }

            if ((minErrorState == maxState) || _NewViterbiService.extendedWindow >= 15) {
                for (int t = 0; t < path.length; t++) {
                    result.add(closestPoints[t][path[t]]);
                }
                gpsErrorInitial = gpsErrorOfPaths.get(maxState);
                _NewViterbiService.prev_MLP = _NewViterbiService.most_likely_path;
                _NewViterbiService.most_likely_path = stateToSegmentID.get(maxState);
                _NewViterbiService.visited.clear();
                if (_NewViterbiService.prev_MLP != -1) {
                    Point connectionPoint = connectionPoint(_NewViterbiService.prev_MLP, _NewViterbiService.most_likely_path, _NewViterbiService.segmentMapping);
                    _NewViterbiService.visited.addAll(visitedPoints(connectionPoint, result.get(result.size() - 1), _NewViterbiService.most_likely_path, _NewViterbiService.segmentMapping));
                } else {
                    _NewViterbiService.visited.addAll(visitedPoints(result));
                }
                _NewViterbiService.extendedWindow = 5;
                _NewViterbiService.checkWindowExtention = false;
                _NewViterbiService.checkIntersectionInsideWindow = false;
                //System.out.println(_NewViterbiService.most_likely_path);
                return result;
            } else {
                result = null;
                int window = ViterbiController.observations.size() + 5;
                _NewViterbiService.extendedWindow = Math.min(window, 15);
                _NewViterbiService.checkWindowExtention = true;
                return result;
            }
        }
    }

    public boolean isIntersectionWindow(int prevSegment, int currSegment) {
        boolean isConnection = _ConnectionOrIntersectionService.connectionExist(prevSegment, currSegment, segmentGraph);

        if (isConnection) {
            boolean isIntersection = _ConnectionOrIntersectionService.intersectionExist(prevSegment, currSegment, roadGraph);
            if (isIntersection) {
                return true;
            }
        }

        Set<Integer> moreConnectedVertices = _ApplyViterbiService.getVerticesAtDistance(segmentGraph, currSegment, 4);
        DijkstraShortestPath<Integer, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(SegmentGraph.getGraph());

        if (moreConnectedVertices.contains(prevSegment)) {
            GraphPath<Integer, DefaultEdge> path = dijkstraAlg.getPath(prevSegment, currSegment);
            List<Integer> pathList = path.getVertexList();

            for (int i = 0; i < pathList.size() - 1; i++) {
                int curr = pathList.get(i);
                int next = pathList.get(i + 1);

                boolean isIntersectionCheck = _ConnectionOrIntersectionService.intersectionExist(curr, next, roadGraph);
                if (isIntersectionCheck) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Double> computeGPSError(int[][] backpointer, int maxState, Map<Integer, Integer> segmentToState, Point[][] closestPoint, List<Double[]> gpsCoordinates) {

        List<Double> gpsError = new ArrayList<>();
        for (int i = 0; i < backpointer[0].length; i++) {
            boolean flag = false;
            int[] path = new int[gpsCoordinates.size()];
            path[gpsCoordinates.size() - 1] = backpointer[backpointer.length - 1][i];

            for (int t = gpsCoordinates.size() - 2; t >= 0; t--) {
                if (path[t + 1] == -1) {
                    flag = true;
                    break;
                }
                path[t] = backpointer[t + 1][path[t + 1]];
            }

            if (flag) {
                gpsError.add(-1.0);
                continue;
            }

            double error = 0.0;


            for (int j = 0; j < path.length; j++) {
                if (path[j] == -1) {
                    break;
                }

                error += Point.haversineDistance(new Point(gpsCoordinates.get(j)[0], gpsCoordinates.get(j)[1]), closestPoint[j][path[j]]);
            }

            gpsError.add((error / (double) (gpsCoordinates.size())));
        }
        return gpsError;
    }

    public static Point connectionPoint(int prev_MLP, int curr_MLP, Map<Integer, List<List<Double>>> segmentMapping) {
        List<Double> startPointOfPrevMLP = segmentMapping.get(prev_MLP).get(0);
        List<Double> endPointOfPrevMLP = segmentMapping.get(prev_MLP).get(segmentMapping.get(prev_MLP).size() - 1);
        List<Double> startPointOfCurrMLP = segmentMapping.get(curr_MLP).get(0);
        List<Double> endPointOfCurrMLP = segmentMapping.get(curr_MLP).get(segmentMapping.get(curr_MLP).size() - 1);

        if ((startPointOfPrevMLP.get(0).equals(startPointOfCurrMLP.get(0))) && (startPointOfPrevMLP.get(1).equals(startPointOfCurrMLP.get(1)))) {
            return new Point(startPointOfPrevMLP.get(0), startPointOfPrevMLP.get(1));
        } else if ((startPointOfPrevMLP.get(0).equals(endPointOfCurrMLP.get(0))) && (startPointOfPrevMLP.get(1).equals(endPointOfCurrMLP.get(1)))) {
            return new Point(startPointOfPrevMLP.get(0), startPointOfPrevMLP.get(1));
        } else if ((endPointOfPrevMLP.get(0).equals(startPointOfCurrMLP.get(0))) && (endPointOfPrevMLP.get(1).equals(startPointOfCurrMLP.get(1)))) {
            return new Point(endPointOfPrevMLP.get(0), endPointOfPrevMLP.get(1));
        } else {
            return new Point(endPointOfPrevMLP.get(0), endPointOfPrevMLP.get(1));
        }
    }

    public static Set<String> visitedPoints(Point connectionPoint, Point endPoint, int curr_MLP, Map<Integer, List<List<Double>>> segmentMapping) {
        Set<String> s = new LinkedHashSet();
        List<List<Double>> segment = segmentMapping.get(curr_MLP);
        int segmentLength = segment.size();
        int index = -1;
        if ((Objects.equals(segment.get(0).get(0), connectionPoint.getLongitude())) && (Objects.equals(segment.get(0).get(1), connectionPoint.getLatitude()))) {
            index = 0;
        } else {
            index = segmentLength - 1;
        }
        System.out.println("filing visited points.....");

        if (index == 0) {
            for (int i = index; i < segmentLength; i++) {
                s.add(segment.get(i).get(0) + "#" + segment.get(i).get(1));

                if(i == index){
                    _NewViterbiService.firstInVisited = new Point(segment.get(i).get(0), segment.get(i).get(1));
                }
                if ((segment.get(i).get(0).equals(endPoint.getLongitude())) && (segment.get(i).get(1).equals(endPoint.getLatitude()))) {
                    _NewViterbiService.lastInVisited =  new Point(endPoint.getLongitude(), endPoint.getLatitude());
                    break;
                }
            }
        } else {
            for (int i = index; i >= 0; i--) {
                s.add(segment.get(i).get(0) + "#" + segment.get(i).get(1));

                if(i == index){
                    _NewViterbiService.firstInVisited = new Point(segment.get(i).get(0), segment.get(i).get(1));
                }
                if ((segment.get(i).get(0).equals(endPoint.getLongitude())) && (segment.get(i).get(1).equals(endPoint.getLatitude()))) {
                    _NewViterbiService.lastInVisited =  new Point(endPoint.getLongitude(), endPoint.getLatitude());
                    break;
                }
            }
        }
        System.out.println("lastInVisited : " + _NewViterbiService.lastInVisited);
        return s;
    }

    public static Set<String> visitedPoints(List<Point> result) {
        System.out.println("abcd.....here");
        Set<String> s = new LinkedHashSet();
        List<List<Double>> segment = _NewViterbiService.segmentMapping.get(_NewViterbiService.most_likely_path);
        int segmentLength = segment.size();

        Point begin = result.get(0);
        Point end = result.get(result.size() - 1);
        int beginIndex = -1;
        int endIndex = -1;

        for(int i = 0; i < segmentLength; i++){
            List<Double> coordinate = segment.get(i);
            if(begin.getLongitude().equals(coordinate.get(0)) && begin.getLatitude().equals(coordinate.get(1))){
                beginIndex = i;
            }
            if(end.getLongitude().equals(coordinate.get(0)) && end.getLatitude().equals(coordinate.get(1))){
                endIndex = i;
            }
        }

        if(beginIndex < endIndex){
            for(int i = beginIndex; i <= endIndex; i++){
                if(i == endIndex - 1){
                    _NewViterbiService.secondlastInVisited = new Point(segment.get(i).get(0),segment.get(i).get(1));
                }
                s.add(segment.get(i).get(0) + "#" + segment.get(i).get(1));
            }
        }
        else{
            for(int i = beginIndex; i >= endIndex; i--){
                if(i == endIndex + 1){
                    _NewViterbiService.secondlastInVisited = new Point(segment.get(i).get(0),segment.get(i).get(1));
                }
                s.add(segment.get(i).get(0) + "#" + segment.get(i).get(1));
            }
        }
        _NewViterbiService.firstInVisited = result.get(0);
        _NewViterbiService.lastInVisited = result.get(result.size() - 1);
        System.out.println("lastInVisited : " + _NewViterbiService.lastInVisited);
        return s;
    }

    public static Set<String> visitedPoints(Point nearestPointOnMLP){
        Set<String> s = new LinkedHashSet();
        List<List<Double>> segment = _NewViterbiService.segmentMapping.get(_NewViterbiService.most_likely_path);
        int segmentLength = segment.size();
        System.out.println("lastInVisited : " +  _NewViterbiService.lastInVisited.getLongitude() + "," + _NewViterbiService.lastInVisited.getLatitude());
        System.out.println("nearestPointOnMLP : " + nearestPointOnMLP.getLongitude() + "," + nearestPointOnMLP.getLatitude());


        int indexOfNearestPointOnMLP = -1;
        int indexOfLastInVisited = -1;

        for(int i = 0; i < segmentLength; i++){
            List<Double> coordinate = segment.get(i);
            if(nearestPointOnMLP.getLongitude().equals(coordinate.get(0)) && nearestPointOnMLP.getLatitude().equals(coordinate.get(1))){
                indexOfNearestPointOnMLP = i;
            }

            if(_NewViterbiService.lastInVisited.getLongitude().equals(coordinate.get(0)) && _NewViterbiService.lastInVisited.getLatitude().equals(coordinate.get(1))){
                indexOfLastInVisited = i;
            }
        }

        if(indexOfLastInVisited > indexOfNearestPointOnMLP){
            for(int j = indexOfLastInVisited; j >= indexOfNearestPointOnMLP; j--){
                List<Double> coordinate = segment.get(j);
                s.add(coordinate.get(0) + "#" + coordinate.get(1));
            }
        }
        else{
            for(int j = indexOfLastInVisited; j <= indexOfNearestPointOnMLP; j++){
                List<Double> coordinate = segment.get(j);
                s.add(coordinate.get(0) + "#" + coordinate.get(1));
            }
        }
        _NewViterbiService.lastInVisited = nearestPointOnMLP;
        return s;
    }
}
