package com.example.SpringMapMatching.Service;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class _ProhibitedNodes {

    // Function to find the vertex with degree >= 3 using DFS
    public static List<Integer> findVertexWithDegreeThreeOrMore(Graph<Integer, DefaultEdge> graph, Integer startNode) {
        // Get the neighbors of the start node
        List<Integer> result = new ArrayList<>();
        if(graph.degreeOf(startNode) >= 3){
            result.add(startNode);
            return result;
        }

        Set<Integer> neighbors = graph.edgesOf(startNode).stream()
                .map(edge -> graph.getEdgeTarget(edge).equals(startNode) ? graph.getEdgeSource(edge) : graph.getEdgeTarget(edge))
                .collect(java.util.stream.Collectors.toSet());

        // Perform DFS from each neighbor
        for (Integer neighbor : neighbors) {
            Integer intersectionNode = depthFirstSearch(graph, neighbor, startNode);
            if (intersectionNode != null) {
                result.add(intersectionNode);
            }
        }

        // Return null if no vertex with degree >= 3 is found
        return result;
    }

    // Helper function to perform DFS and find vertex with degree >= 3
    private static Integer depthFirstSearch(Graph<Integer, DefaultEdge> graph, Integer currentNode, Integer parentNode) {
        DepthFirstIterator<Integer, DefaultEdge> dfsIterator = new DepthFirstIterator<>(graph, currentNode);

        while (dfsIterator.hasNext()) {
            Integer node = dfsIterator.next();

            if(node == parentNode){
                continue;
            }
            // Check the degree of the current node, skip the parent node to avoid backtracking
            if (!node.equals(parentNode) && graph.degreeOf(node) >= 3) {
                return node;
            }
        }

        // Return null if no vertex with degree >= 3 is found
        return null;
    }
}
