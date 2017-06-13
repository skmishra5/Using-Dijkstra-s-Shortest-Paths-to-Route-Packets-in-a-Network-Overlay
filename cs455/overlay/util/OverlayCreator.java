package cs455.overlay.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OverlayCreator {
	
	private ArrayList<String> m_messagingNodeInfo = new ArrayList<String>();
	private HashMap<String, String> overlayInformation = new HashMap<String, String>();
	
	public OverlayCreator(ArrayList<String> messagingNodeInfo)
	{
		m_messagingNodeInfo = messagingNodeInfo;
	}
	
	public HashMap<String, String> createOverlay()
	{
		String firstNode = "";
		String secondNode = "";
		String thirdNode = "";
		
		for(int i = 0; i < m_messagingNodeInfo.size(); i++)
		{
			firstNode = m_messagingNodeInfo.get(i);
			if(i == m_messagingNodeInfo.size() - 1)
			{
				secondNode = m_messagingNodeInfo.get(0);
			}
			else
			{
				secondNode = m_messagingNodeInfo.get(i+1);
			}
			
			if(i == m_messagingNodeInfo.size() - 2)
			{
				thirdNode = m_messagingNodeInfo.get(0);
			}
			else if(i == m_messagingNodeInfo.size() - 1)
			{
				thirdNode = m_messagingNodeInfo.get(1);
			}
			else
			{
				thirdNode = m_messagingNodeInfo.get(i+2);
			}
			
			String[] tokenFirst = firstNode.split(":");
			String[] tokenSecond = secondNode.split(":");
			String[] tokenThird = thirdNode.split(":");

			overlayInformation.put(tokenFirst[0] + ":" + tokenFirst[1], tokenSecond[0] + ":" + tokenSecond[2] + "," 
					+ tokenThird[0] + ":" + tokenThird[2]);
		}
		
		return overlayInformation;
	}
}
