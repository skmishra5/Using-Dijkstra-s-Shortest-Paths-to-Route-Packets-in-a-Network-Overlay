package cs455.overlay.dijkstra;

import java.util.ArrayList;

public class OverlayGraph {
	
	private ArrayList<Vertex> m_vertices;
	private ArrayList<Link> m_links;
	
	public OverlayGraph(ArrayList<Vertex> vertices, ArrayList<Link> links)
	{
		this.m_vertices = vertices;
		this.m_links = links;
	}
	
	public ArrayList<Vertex> getVertices()
	{
		return m_vertices;
	}
	
	public ArrayList<Link> getLinks()
	{
		return m_links;
	}
}
