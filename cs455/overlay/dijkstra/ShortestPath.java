package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class ShortestPath {
	
	private ArrayList<Vertex> m_nodes;
	private ArrayList<Link> m_links;
	private HashSet<Vertex> m_visitedNodes;
	private HashSet<Vertex> m_notVisitedNodes;
	private HashMap<Vertex, Vertex> m_predecessors;
	private HashMap<Vertex, Integer> m_linkWeights;
	
	public ShortestPath(OverlayGraph overlayGraph)
	{
		this.m_nodes = new ArrayList<Vertex>(overlayGraph.getVertices());
		this.m_links = new ArrayList<Link>(overlayGraph.getLinks());
	}
	
	public void startCalculate(Vertex sourceNode)
	{
		m_visitedNodes = new HashSet<Vertex>();
		m_notVisitedNodes = new HashSet<Vertex>();
		m_predecessors = new HashMap<Vertex, Vertex>();
		m_linkWeights = new HashMap<Vertex, Integer>();
		
		m_linkWeights.put(sourceNode, 0);
		m_notVisitedNodes.add(sourceNode);
		
		while (m_notVisitedNodes.size() > 0) {
            Vertex node = getMinimum(m_notVisitedNodes);
            m_visitedNodes.add(node);
            m_notVisitedNodes.remove(node);
            findMinimalDistances(node);
		}
    }
		
	private Vertex getMinimum(HashSet<Vertex> vertices) {

        Vertex min = null;
        for (Vertex vertex : vertices) {
        	if (min == null) {
        		min = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(min)) {
                    min = vertex;
                }
            }
        }
        return min;
    }
	
	private int getShortestDistance(Vertex destination) {

        Integer linkWeight = m_linkWeights.get(destination);
        if (linkWeight == null) {
                return Integer.MAX_VALUE;
        } else {
                return linkWeight;
        }
	}
	
	private void findMinimalDistances(Vertex node) {

        ArrayList<Vertex> adjacentNodes = getNeighbors(node);
        for (Vertex target : adjacentNodes) {
                if (getShortestDistance(target) > getShortestDistance(node)
                                + getDistance(node, target)) {
                	m_linkWeights.put(target, getShortestDistance(node)
                                + getDistance(node, target));
                	m_predecessors.put(target, node);
                	m_notVisitedNodes.add(target);
                }
        }

	}
	
	private ArrayList<Vertex> getNeighbors(Vertex node) {

        ArrayList<Vertex> neighbors = new ArrayList<Vertex>();
        for (Link link : m_links) {
                if (link.getSourceNode().equals(node) && !isSettled(link.getDestinationNode())) {
                        neighbors.add(link.getDestinationNode());
                }
        }
        return neighbors;
	}
	
	private boolean isSettled(Vertex vertex) {

        return m_visitedNodes.contains(vertex);
	}
	
	private int getDistance(Vertex node, Vertex target) {

        for (Link link : m_links) {
                if (link.getSourceNode().equals(node)
                                && link.getDestinationNode().equals(target)) {
                        return link.getLinkWeight();
                }
        }
        throw new RuntimeException("Something wrong!");
	}
	
	public LinkedList<Vertex> getPath(Vertex target) {
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        Vertex step = target;
        // check if a path exists
        if (m_predecessors.get(step) == null) {
                return null;
        }
        path.add(step);
        while (m_predecessors.get(step) != null) {
                step = m_predecessors.get(step);
                path.add(step);
        }
        
        // Put it into the correct order
        Collections.reverse(path);
        return path;
}
	
	
}
