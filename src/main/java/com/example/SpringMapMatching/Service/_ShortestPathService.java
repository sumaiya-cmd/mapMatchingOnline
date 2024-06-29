package com.example.SpringMapMatching.Service;

import com.example.SpringMapMatching.Database.Data;
import com.example.SpringMapMatching.Database.LocationNavPath;
import com.example.SpringMapMatching.Model.Point;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class _ShortestPathService {
    private static Data data;
    public static Point source = null;
    public static Point destination = null;
    private static RoadGraph roadGraph;

    @Autowired
    public _ShortestPathService(Data data, RoadGraph roadGraph, SegmentGraph segmentGraph){
        this.data = data;
        this.roadGraph = roadGraph;
    }

    //static Map<Integer, List<List<Double>>> segmentMapping = new HashMap<>();
    public static int restrictedSegment = -1;
    public static Set<String> restrictedPoints = new HashSet<>();
    public static Set<String> alertPoints = new HashSet<>();

    public List<Point> findShortestDistance(Point sourceOnMap, Point destinationOnMap){
        Point source = findPointOnGraph(sourceOnMap);
        Point destination = findPointOnGraph(destinationOnMap);

        String sourceVertex = source.getLongitude() + "#" + source.getLatitude();
        String destinationVertex = destination.getLongitude() + "#" + destination.getLatitude();
        List<Point> path = new ArrayList<>();
        if(restrictedPoints.isEmpty()){
            path.addAll(shortestPathWithoutRestrictedPointsAndAlerts(sourceVertex, destinationVertex, roadGraph));
        }
        else{
            path.addAll(shortestPathWithRestrictedPointAndAlerts(sourceVertex, destinationVertex, roadGraph));
        }
        System.out.println("size of retuened path : " + path.size());
        return path;
    }

    public static Point findAndSetAlertPoint(Double[] coordinate){
        List<LocationNavPath> nearestRoadSegmentToCoordinate = data.findByLocationNear(coordinate[0], coordinate[1], 100);
        restrictedSegment = nearestRoadSegmentToCoordinate.get(0).getH_ID();
        restrictedPoints.addAll(findRestrictedPoints(restrictedSegment));
        Point alert = findPointOnGraph(new Point(coordinate[0], coordinate[1]));
        alertPoints.add(alert.getLongitude() + "#" + alert.getLatitude());
        return alert;
    }

    public static Point findPointOnGraph(Point coordinate){
        List<LocationNavPath> nearestRoadSegmentToCoordinate = data.findByLocationNear(coordinate.getLongitude(), coordinate.getLatitude(), 100);

        int id = nearestRoadSegmentToCoordinate.get(0).getH_ID();
        List<List<Double>> roadSegment = _NewViterbiService.segmentMapping.get(id);
        Point closestPoint = _MappingWithoutViterbiService.findNearestPointFromRoad(coordinate, roadSegment);
        return closestPoint;
    }

    public static Set<String> findRestrictedPoints(int restrictedSegment){
        Set<String> restrictedPoints = new HashSet<>();
        List<Integer> segmentsHavingIntersection = _ProhibitedNodes.findVertexWithDegreeThreeOrMore(SegmentGraph.getGraph(), restrictedSegment);

        for(int segmentId : segmentsHavingIntersection){
            //System.out.println(segmentId);
            //System.out.println(_NewViterbiService.segmentMapping.size());
            List<List<Double>> segmentCoordinates = _NewViterbiService.segmentMapping.get(segmentId);
            List<Double> start = segmentCoordinates.get(1);
            List<Double> end = segmentCoordinates.get(segmentCoordinates.size() - 2);

            for(int i = 0; i < segmentCoordinates.size(); i += segmentCoordinates.size() - 1){
                List<Double> coordinate = segmentCoordinates.get(i);
                boolean isIntersectionPoint = _ConnectionOrIntersectionService.checkIntersectionPoint(coordinate, roadGraph);
                if(isIntersectionPoint){
                    if(i == 0){
                        System.out.println("restricted");
                        System.out.println(start.get(0) + "," + start.get(1));
                        restrictedPoints.add(start.get(0) + "#" + start.get(1));
                    }
                    if(i == segmentCoordinates.size() - 1){
                        System.out.println("restricted");
                        System.out.println(end.get(0)+ "," + end.get(1));
                        restrictedPoints.add(end.get(0)+ "#" + end.get(1));
                    }
                }
            }
        }
        return restrictedPoints;
    }

    public static List<Point> shortestPathWithRestrictedPointOld(String source, String destination, RoadGraph roadGraph) {

        // Create an adjacency list of pairs of the form node1 -> {node2, edge weight}
        // where the edge weight is the weight of the edge from node1 to node2.

        Graph<String, DefaultWeightedEdge> graph = roadGraph.getRoadGraph();
        Set<String> vertices = graph.vertexSet();
        int numberOfVertices = vertices.size();

        // Create a priority queue for storing the nodes along with distances
        // in the form of a pair { dist, node }.
        PriorityQueue<Pair> pq = new PriorityQueue<>(new PairComparator());

        // Create a dist array for storing the updated distances and a parent array
        //for storing the nodes from where the current nodes represented by indices of
        // the parent array came from.
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        dist.put(source, 0.0);

        // Push the source node to the queue.
        pq.add(new Pair(0.0, source));
        while(!pq.isEmpty()) {

            // Topmost element of the priority queue is with minimum distance value.
            Pair it = pq.peek();
            String node = it.second;
            Double dis = it.first;
            pq.remove();

            // Iterate through the adjacent nodes of the current popped node.
            for (DefaultWeightedEdge edge : graph.edgesOf(node)) {
                String sourceVertex = graph.getEdgeSource(edge);
                String adjNode = graph.getEdgeTarget(edge);
                Double edW = graph.getEdgeWeight(edge);

                //System.out.println("source vertex : " + sourceVertex);
                if(!restrictedPoints.contains(adjNode)){
                    //System.out.println("adjNode : " + adjNode);
                    if(dist.get(adjNode) != null){
                        if(dis + edW < dist.get(adjNode)) {
                            dist.put(adjNode, dis + edW);
                            pq.add(new Pair(dis + edW, adjNode));
                            parent.put(adjNode, node);
                        }
                    }
                    else{
                        dist.put(adjNode, dis + edW);
                        pq.add(new Pair(dis + edW, adjNode));
                        parent.put(adjNode, node);
                    }
                }
            }
        }

        // Store the final path in the ‘path’ array.
        List<Point> path = new ArrayList<>();

        // If distance to a node could not be found, return an array containing -1.

        if(dist.get(destination) == null) {
            return path;
        }

        String node = destination;

        while(!Objects.equals(parent.get(node), node)) {
            String[] parts = node.split("#");
            Double longitude = Double.parseDouble(parts[0]);
            Double latitude = Double.parseDouble(parts[1]);
            path.add(new Point(longitude, latitude));
            node = parent.get(node);
        }
        String[] parts = source.split("#");
        Double longitude = Double.parseDouble(parts[0]);
        Double latitude = Double.parseDouble(parts[1]);
        path.add(new Point(longitude, latitude));

        // Since the path stored is in a reverse order, we reverse the array
        // to get the final answer and then return the array.
        Collections.reverse(path);
        return path;
    }

    public static List<Point> shortestPathWithRestrictedPointAndAlerts(String source, String destination, RoadGraph roadGraph) {

        Graph<String, DefaultWeightedEdge> graph = roadGraph.getRoadGraph();
        Map<String, List<Tuple>> adjacencyList = roadGraph.getAdjacencyList();
        Set<String> vertices = graph.vertexSet();
        int numberOfVertices = vertices.size();

        // Create a priority queue for storing the nodes along with distances
        // in the form of a pair { dist, node }.
        PriorityQueue<Pair> pq = new PriorityQueue<>(new PairComparator());

        // Create a dist array for storing the updated distances and a parent array
        //for storing the nodes from where the current nodes represented by indices of
        // the parent array came from.
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        dist.put(source, 0.0);

        // Push the source node to the queue.
        pq.add(new Pair(0.0, source));
        while(!pq.isEmpty()) {

            // Topmost element of the priority queue is with minimum distance value.
            Pair it = pq.peek();
            String node = it.second;
            Double dis = it.first;
            pq.remove();

//            System.out.println("source vertex : " + node);
//            for (DefaultWeightedEdge edge : graph.edgesOf(node)){
//                String sourceVertex = graph.getEdgeSource(edge);
//                String adjNode = graph.getEdgeTarget(edge);
//                Double edW = graph.getEdgeWeight(edge);
//                System.out.println("adj node : " + adjNode);
//            }

            // Iterate through the adjacent nodes of the current popped node.
            for (Tuple t : adjacencyList.get(node)) {
                String sourceVertex = node;
                String adjNode = t.getFirst();
                Double edW = t.getSecond();

                //System.out.println("source vertex : " + sourceVertex);
                //System.out.println("adj node : " + adjNode);

                if(alertPoints.contains((adjNode))){
                    continue;
                }
                else if(restrictedPoints.contains(adjNode)){
                    continue;
                }
                else{
                    //System.out.println();
                    if(dist.get(adjNode) != null){
                        if(dis + edW < dist.get(adjNode)) {
                            dist.put(adjNode, dis + edW);
                            pq.add(new Pair(dis + edW, adjNode));
                            parent.put(adjNode, node);
                        }
                    }
                    else{
                        dist.put(adjNode, dis + edW);
                        pq.add(new Pair(dis + edW, adjNode));
                        parent.put(adjNode, node);
                    }
                }
            }
        }

        // Store the final path in the ‘path’ array.
        List<Point> path = new ArrayList<>();

        // If distance to a node could not be found, return an array containing -1.
        if(dist.get(destination) == null){
            System.out.println("alerts and restrictions found");
            path = shortestPathWithAlerts(source, destination, roadGraph);
            return path;
        }

        String node = destination;

        while(!Objects.equals(parent.get(node), node)) {
            String[] parts = node.split("#");
            Double longitude = Double.parseDouble(parts[0]);
            Double latitude = Double.parseDouble(parts[1]);
            path.add(new Point(longitude, latitude));
            node = parent.get(node);
        }
        System.out.println("size of path : " + path.size());

//        if(path.isEmpty()){
//            System.out.println("alerts and restrictions found");
//            path = shortestPathWithAlerts(source, destination, roadGraph);
//
//            if(path.isEmpty()){
//                System.out.println("alerts found");
//                path = shortestPathWithoutRestrictedPointsAndAlerts(source, destination, roadGraph);
//                System.out.println("here :" + path.size());
//            }
//        }

        String[] parts = source.split("#");
        Double longitude = Double.parseDouble(parts[0]);
        Double latitude = Double.parseDouble(parts[1]);
        path.add(new Point(longitude, latitude));

        // Since the path stored is in a reverse order, we reverse the array
        // to get the final answer and then return the array.

        Collections.reverse(path);
        return path;
    }

    public static List<Point> shortestPathWithAlerts(String source, String destination, RoadGraph roadGraph) {

        // Create an adjacency list of pairs of the form node1 -> {node2, edge weight}
        // where the edge weight is the weight of the edge from node1 to node2.

        Graph<String, DefaultWeightedEdge> graph = roadGraph.getRoadGraph();
        Map<String, List<Tuple>> adjacencyList = roadGraph.getAdjacencyList();
        Set<String> vertices = graph.vertexSet();
        int numberOfVertices = vertices.size();

        // Create a priority queue for storing the nodes along with distances
        // in the form of a pair { dist, node }.
        PriorityQueue<Pair> pq = new PriorityQueue<>(new PairComparator());

        // Create a dist array for storing the updated distances and a parent array
        //for storing the nodes from where the current nodes represented by indices of
        // the parent array came from.
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        dist.put(source, 0.0);

        // Push the source node to the queue.
        pq.add(new Pair(0.0, source));
        while(!pq.isEmpty()) {

            // Topmost element of the priority queue is with minimum distance value.
            Pair it = pq.peek();
            String node = it.second;
            Double dis = it.first;
            pq.remove();

            // Iterate through the adjacent nodes of the current popped node.
            for (Tuple t : adjacencyList.get(node)) {
                String sourceVertex = node;
                String adjNode =t.getFirst();
                Double edW = t.getSecond();

                //System.out.println("source vertex : " + sourceVertex);
                if(!alertPoints.contains(adjNode)){
                    //System.out.println("adjNode : " + adjNode);
                    if(dist.get(adjNode) != null){
                        if(dis + edW < dist.get(adjNode)) {
                            dist.put(adjNode, dis + edW);
                            pq.add(new Pair(dis + edW, adjNode));
                            parent.put(adjNode, node);
                        }
                    }
                    else{
                        dist.put(adjNode, dis + edW);
                        pq.add(new Pair(dis + edW, adjNode));
                        parent.put(adjNode, node);
                    }
                }
            }
        }

        // Store the final path in the ‘path’ array.
        List<Point> path = new ArrayList<>();

        // If distance to a node could not be found, return an array containing -1.

        if(dist.get(destination) == null) {
            System.out.println("alerts found");
            path = shortestPathWithoutRestrictedPointsAndAlerts(source, destination, roadGraph);
            System.out.println("here :" + path.size());
            return path;
        }

        String node = destination;

        while(!Objects.equals(parent.get(node), node)) {
            String[] parts = node.split("#");
            Double longitude = Double.parseDouble(parts[0]);
            Double latitude = Double.parseDouble(parts[1]);
            path.add(new Point(longitude, latitude));
            node = parent.get(node);
        }
        String[] parts = source.split("#");
        Double longitude = Double.parseDouble(parts[0]);
        Double latitude = Double.parseDouble(parts[1]);
        path.add(new Point(longitude, latitude));

        // Since the path stored is in a reverse order, we reverse the array
        // to get the final answer and then return the array.
        Collections.reverse(path);
        return path;
    }



    public static List<Point> shortestPathWithoutRestrictedPointsAndAlerts(String source, String destination, RoadGraph roadGraph){
        Graph<String, DefaultWeightedEdge> graph = roadGraph.getRoadGraph();
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        List<String> shortestPathInStringFormat = dijkstraShortestPath.getPath(source, destination).getVertexList();

        List<Point> shortestPath = new ArrayList<>();

        for (String vertexInPath : shortestPathInStringFormat){
            String[] parts = vertexInPath.split("#", 2);
            shortestPath.add(new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
        }
        return shortestPath;
    }

    public static void main(String[] args){
        String source = 76.66672750846479 + "#" + 12.389562739415165;
        String destination = 76.6672042234933 + "#" + 12.393199956909855;
        restrictedPoints.add(76.66686014689013 + "#" + 12.392021147903757);
        List<Point> path = shortestPathWithRestrictedPointAndAlerts(source, destination,roadGraph);
        _NewViterbiService.printResult(path);
    }
}

class Pair{
    Double first;
    String second;
    public Pair(Double first,String second){
        this.first = first;
        this.second = second;
    }
}

class PairComparator implements Comparator<Pair> {
    @Override
    public int compare(Pair p1, Pair p2) {
        int firstComparison = p1.first.compareTo(p2.first);
        if (firstComparison != 0) {
            return firstComparison;
        }
        return p1.second.compareTo(p2.second);
    }
}

//source vertex : 76.85679122300117#12.515721297371165
//adj node : 76.85679122300117#12.515721297371165
//adj node : 76.85698071166121#12.51581918822306
//
//
//source vertex : 76.85698071166121#12.51581918822306
//adj node : 76.85698071166121#12.51581918822306
//adj node : 76.85698071166121#12.51581918822306
//adj node : 76.857183478373#12.51592393846141