package com.example.SpringMapMatching.Service;


import com.example.SpringMapMatching.Database.Data;
import com.example.SpringMapMatching.Database.Geo;
import com.example.SpringMapMatching.Database.LocationNavPath;
import com.example.SpringMapMatching.Model.Point;
import jakarta.annotation.PostConstruct;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoadGraph {

    @Autowired
    private Data data;

    private Graph<String, DefaultWeightedEdge> graph;

    private Point point;
    private Map<String, List<Tuple>> adjacencyList = new HashMap<>();

    @PostConstruct
    public void init() {
        graph = buildRoadGraph();

        point = new Point(0.0,0.0);
//        System.out.println("ROAD GRAPH CREATED");
//        printRoadVertex();
    }

    public Graph<String, DefaultWeightedEdge> getRoadGraph() {
        return graph;
    }

    private Graph<String, DefaultWeightedEdge> buildRoadGraph() {
        // Create a weighted graph
        Graph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // Fetch road segments data using your Data repository
        List<LocationNavPath> roadSegments = data.findAll(); // Example method, adjust as needed

        _NewViterbiService.segmentsMap = data.findAll();

        if(!_NewViterbiService.segmentsMap.isEmpty()) {
            System.out.println("making the segment mapping");
            for (LocationNavPath segment : _NewViterbiService.segmentsMap) {
                _NewViterbiService.segmentMapping.put(segment.getH_ID(), segment.getGeo().getCoordinates());
            }
            System.out.println("size of segment mapping : " + _NewViterbiService.segmentMapping.size());
        } else{
            System.out.println("No segments found in the database.");
        }
        // Set to keep track of unique vertices
        Set<String> uniqueVertices = new HashSet<>();

        for (LocationNavPath segment : roadSegments) {
            Geo geometry = segment.getGeo();
            List<List<Double>> coordinates = geometry.getCoordinates();

            // Add vertices (coordinate points) to the graph
            for (List<Double> coordinate : coordinates) {
                String vertex = coordinate.get(0) + "#" + coordinate.get(1);
                if (!uniqueVertices.contains(vertex)) {
                    graph.addVertex(vertex);
                    uniqueVertices.add(vertex); // Add the vertex to the set
                }
            }

            // Add edges (connections between adjacent points) to the graph
            for (int i = 0; i < coordinates.size() - 1; i++) {
                List<Double> sourceVertex = coordinates.get(i);
                List<Double> targetVertex = coordinates.get(i+1);
                String sv = sourceVertex.get(0) + "#" + sourceVertex.get(1);
                String tv = targetVertex.get(0) + "#" + targetVertex.get(1);

                // Calculate distance between two points using Haversine formula
                double distance = point.haversineDistance(new Point(sourceVertex.get(0), sourceVertex.get(1)), new Point(targetVertex.get(0), targetVertex.get(1)));

                // Add undirected edge with weight
                DefaultWeightedEdge edge = graph.addEdge(sv, tv);
                graph.setEdgeWeight(edge, distance);
            }
        }

        for (String vertex : graph.vertexSet()) {
            List<Tuple> adjacentVertices = new ArrayList<>();
            for (DefaultWeightedEdge edge : graph.edgesOf(vertex)) {
                String source = graph.getEdgeSource(edge);
                String target = graph.getEdgeTarget(edge);
                String adjacentVertex = source.equals(vertex) ? target : source;
                double weight = graph.getEdgeWeight(edge);
                adjacentVertices.add(new Tuple(adjacentVertex,weight));
            }
            adjacencyList.put(vertex, new ArrayList<>(adjacentVertices));
        }
        return graph;
    }

    public double getShortestDistance(Graph<String, DefaultWeightedEdge> graph,String sourceVertex, String targetVertex) {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
        return dijkstra.getPathWeight(sourceVertex, targetVertex);
    }

    public Map<String, List<Tuple>> getAdjacencyList(){
        return adjacencyList;
    }

    public void printRoadVertex(){
//        System.out.println("Vertices: " + graph.vertexSet().size());
    }
}

class Tuple {
    private final String first;
    private final Double second;

    public Tuple(String first, Double second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public Double getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple tuple = (Tuple) o;

        if (!first.equals(tuple.first)) return false;
        return second.equals(tuple.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return (String) ("(" + first + ", " + second + ")");
    }
}


