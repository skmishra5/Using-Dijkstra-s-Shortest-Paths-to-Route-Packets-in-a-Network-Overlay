package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class RoutingCache {
	
	private String m_ownNode;
	private Vertex m_ownVertex;
	private OverlayGraph m_overlayGraph;
	private ShortestPath m_shortestPath;
	private ArrayList<Vertex> m_listOfVertices = new ArrayList<Vertex>();
	private ArrayList<Link> m_listOfLinks = new ArrayList<Link>();
	private static HashMap<String, String> shortestRoutePathStore = new HashMap<String, String>();
	
	
	public RoutingCache(String ownNode, ArrayList<Vertex> listOfVertices, ArrayList<Link> listOfLinks)
	{
		this.m_ownNode = ownNode;
		this.m_listOfVertices= listOfVertices;
		this.m_listOfLinks = listOfLinks;
	}
	
	public HashMap<String, String> getShortestRoutingPath()
	{
		return shortestRoutePathStore;
		
	}
	
	public void storeRoutingInformation()
	{
		m_overlayGraph = new OverlayGraph(m_listOfVertices, m_listOfLinks);
		m_shortestPath = new ShortestPath(m_overlayGraph);
		
		for(Vertex vertex : m_listOfVertices)
		{
			if(vertex.getNodeName().equals(m_ownNode.trim()))
			{
				m_ownVertex = vertex;
			}
		}
		
		m_shortestPath.startCalculate(m_listOfVertices.get(0));
		for(int i = 1; i < m_listOfVertices.size(); i++)
		{
			LinkedList<Vertex> path = m_shortestPath.getPath(m_listOfVertices.get(i));
			System.out.println("\n");
			String temp = "";
			for(Vertex v: path)
			{	
				System.out.print(v + " ");
				temp += v.getNodeName() + " ";
			}
			String[] token = temp.split(" ");
			String route = "";
			for(int j = 1; j < token.length ; j++)
			{
				route += token[j] + " ";
			}
			shortestRoutePathStore.put(token[token.length -1], route);
		}
		
		Set set = shortestRoutePathStore.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
			System.out.println("key is: "+ mentry.getKey() + " & Value is: ");
			System.out.println(mentry.getValue());			
		}
		
		/*for(Vertex vertex : m_listOfVertices)
		{
			if(vertex.getNodeName() != m_ownVertex.getNodeName())
			{
				LinkedList<Vertex> path = m_shortestPath.getPath(vertex);
				for(Vertex v: path)
				{
					System.out.print(v);
				}
				shortestRoutePathStore.add(path);
			}
			System.out.println("\n");
		}
		
		for(LinkedList<Vertex> lv : shortestRoutePathStore)
		{
			System.out.print("\n");
			for(Vertex vertex: lv)
			{
				System.out.print(vertex);
				System.out.print(" ");
			}
		}*/
	}
	
}
