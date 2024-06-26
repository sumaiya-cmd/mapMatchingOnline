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
            if (viterbi[viterbi.length- 1][j] != 0.0 && viterbi[viterbi.length - 1][j] > maxProb) {
                maxProb = viterbi[viterbi.length- 1][j];
                maxState = j;
            }
        }

        //most_likely_path = stateToSegmentID.get(maxState);


        int[] path = new int[gpsCoordinates.size()];
        path[gpsCoordinates.size() - 1] = maxState;



        for (int t = gpsCoordinates.size() - 2; t >= 0; t--) {
            path[t] = backpointer[t + 1][path[t + 1]];
        }

//        for(int t = 0; t < path.length; t++){
//            System.out.print(stateToSegmentID.get(path[t]) + " ");
//        }
//        System.out.println("------------------------");

        for(int t = 1; t < gpsCoordinates.size(); t++){
            if(stateToSegmentID.get(path[t - 1]) != stateToSegmentID.get(path[t])){
                boolean isWindowBreak = isIntersectionWindow(stateToSegmentID.get(path[t - 1]), stateToSegmentID.get(path[t]));
                if(isWindowBreak){
                    System.out.println("window breaking");
                    System.out.println(stateToSegmentID.get(path[t - 1]) + " " +  stateToSegmentID.get(path[t]));
                    System.out.println(gpsCoordinates.get(t)[0] + "," + gpsCoordinates.get(t)[1]);
                    System.out.println(gpsCoordinates.get(t - 1)[0] + "," + gpsCoordinates.get(t - 1)[1]);

                    maxProb = Double.NEGATIVE_INFINITY;
                    maxState = -1;

                    for (int k = 0; k < segmentToState.size(); k++) {
                        if (viterbi[t - 1][k] != 0.0 && viterbi[t- 1][k] > maxProb) {
                            maxProb = viterbi[t - 1][k];
                            maxState = k;
                        }
                    }

                    _NewViterbiService.most_likely_path = stateToSegmentID.get(maxState);
                    int[] new_path = new int[t];
                    new_path[t - 1] = maxState;

                    for (int x = t - 2; x >= 0; x--) {
                        new_path[x] = backpointer[x + 1][new_path[x + 1]];
                    }

                    List<Point> result = new ArrayList<>() ;
                    double error = 0.0;
                    for (int x = 0; x < t; x++) {
                        result.add(closestPoints[x][new_path[x]]);
                        if(_NewViterbiService.isBeginning){
                            error += Point.haversineDistance(new Point(gpsCoordinates.get(x)[0], gpsCoordinates.get(x)[1]), closestPoints[x][new_path[x]]);
                        }
                    }

                    if(_NewViterbiService.isBeginning){
                        gpsErrorInitial = error / (double)(t);
                    }
                    _NewViterbiService.makeWindow = t;
                    _NewViterbiService.checkIntersectionInsideWindow = true;
                    _NewViterbiService.extendedWindow = 15;
                    _NewViterbiService.most_likely_path = stateToSegmentID.get(maxState);
                    return result;
                }
                else{
                    continue;
                }
            }
        }

        List<Point> result = new ArrayList<>();

        double error = 0.0;

        List<Double> gpsErrorOfPaths = computeGPSError(backpointer, maxState,segmentToState, closestPoints, gpsCoordinates);

        double minError = Double.POSITIVE_INFINITY;
        int minErrorState = -1;

        if(_NewViterbiService.isBeginning){
            for (int t = 0; t < path.length; t++) {
                result.add(closestPoints[t][path[t]]);
                error += Point.haversineDistance(new Point(gpsCoordinates.get(t)[0], gpsCoordinates.get(t)[1]), closestPoints[t][path[t]]);
            }
            _NewViterbiService.most_likely_path = stateToSegmentID.get(maxState);
            gpsErrorInitial = error / (15.00);
            _NewViterbiService.checkWindowExtention = false;
            return result;
        }
        else{
            for(int k = 0; k < gpsErrorOfPaths.size(); k++){
                double absGPSError = Math.abs(gpsErrorInitial - gpsErrorOfPaths.get(k));
                //double absGPSError = gpsErrorOfPaths.get(k);
                if(absGPSError < minError){
                    minError = absGPSError;
                    minErrorState = k;
                }
            }

            if((minErrorState == maxState) || _NewViterbiService.extendedWindow >= 45){
                for (int t = 0; t < path.length; t++) {
                    result.add(closestPoints[t][path[t]]);
                }
                gpsErrorInitial = gpsErrorOfPaths.get(maxState);
                _NewViterbiService.most_likely_path = stateToSegmentID.get(maxState);
                _NewViterbiService.extendedWindow = 15;
                _NewViterbiService.checkWindowExtention = false;
                _NewViterbiService.checkIntersectionInsideWindow = false;
                //System.out.println(_NewViterbiService.most_likely_path);
                return result;
            }
            else{
                result = null;
                int window = ViterbiController.observations.size() + 15;
                _NewViterbiService.extendedWindow = Math.min(window, 45);
                _NewViterbiService.checkWindowExtention = true;
                return result;
            }
        }
    }

    public boolean isIntersectionWindow(int prevSegment, int currSegment){
        boolean isConnection = _ConnectionOrIntersectionService.connectionExist(prevSegment, currSegment, segmentGraph);

        if(isConnection){
            boolean isIntersection = _ConnectionOrIntersectionService.intersectionExist(prevSegment, currSegment, roadGraph);
            if(isIntersection){
                return true;
            }
        }

        Set<Integer> moreConnectedVertices = _ApplyViterbiService.getVerticesAtDistance(segmentGraph, currSegment, 4);
        DijkstraShortestPath<Integer, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(SegmentGraph.getGraph());

        if(moreConnectedVertices.contains(prevSegment)){
            GraphPath<Integer, DefaultEdge> path = dijkstraAlg.getPath(prevSegment, currSegment);
            List<Integer> pathList = path.getVertexList();

            for(int i = 0; i < pathList.size() - 1; i++) {
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

    public static List<Double> computeGPSError(int [][] backpointer, int maxState, Map<Integer, Integer> segmentToState, Point[][] closestPoint, List<Double[]> gpsCoordinates){

        List<Double> gpsError = new ArrayList<>();
        for(int i = 0; i < backpointer[0].length; i++){
            boolean flag = false;
            int[] path = new int[gpsCoordinates.size()];
            path[gpsCoordinates.size() - 1] = backpointer[backpointer.length - 1][i];

            for (int t = gpsCoordinates.size() - 2; t >= 0; t--) {
                if(path[t + 1] == -1){
                    flag = true;
                    break;
                }
                path[t] = backpointer[t + 1][path[t + 1]];
            }

            if(flag){
                gpsError.add(-1.0);
                continue;
            }

            double error = 0.0;


            for(int j = 0; j < path.length; j++){
                if(path[j] == -1){
                    break;
                }

                error += Point.haversineDistance(new Point(gpsCoordinates.get(j)[0], gpsCoordinates.get(j)[1]), closestPoint[j][path[j]]);
            }

            gpsError.add((error / (double)(gpsCoordinates.size())));
        }
        return gpsError;
    }
}
