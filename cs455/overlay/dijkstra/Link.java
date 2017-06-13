package cs455.overlay.dijkstra;

public class Link {
	
	private Vertex m_sourceNode;
	private Vertex m_destinationNode;
	private int m_linkWeight;
	
	public Link(Vertex sourceNode, Vertex destinationNode, int linkWeight)
	{
		this.m_sourceNode = sourceNode;
		this.m_destinationNode = destinationNode;
		this.m_linkWeight = linkWeight;
	}
	
	public Vertex getSourceNode()
	{
		return m_sourceNode;
	}
	
	public Vertex getDestinationNode()
	{
		return m_destinationNode;
	}
	
	public int getLinkWeight()
	{
		return m_linkWeight;
	}
	
	@Override
    public String toString() {
            return m_sourceNode + " " + m_destinationNode;
    }
}
