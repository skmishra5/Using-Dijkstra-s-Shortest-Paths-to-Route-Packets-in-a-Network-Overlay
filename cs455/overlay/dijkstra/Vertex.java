package cs455.overlay.dijkstra;

public class Vertex {
	
	private String m_id;
	private String m_nodeName;
	
	
	public Vertex(String id, String nodeName)
	{
		this.m_id = id;
		this.m_nodeName = nodeName;
	}
	
	public String getNodeName()
	{
		return m_nodeName;
	}
	
	@Override
    public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((m_id == null) ? 0 : m_id.hashCode());
            return result;
    }

    @Override
    public boolean equals(Object obj) {
            if (this == obj)
                    return true;
            if (obj == null)
                    return false;
            if (getClass() != obj.getClass())
                    return false;
            Vertex other = (Vertex) obj;
            if (m_id == null) {
                    if (other.m_id != null)
                            return false;
            } else if (!m_id.equals(other.m_id))
                    return false;
            return true;
    }

    @Override
    public String toString() {
            return m_nodeName;
    }
}
