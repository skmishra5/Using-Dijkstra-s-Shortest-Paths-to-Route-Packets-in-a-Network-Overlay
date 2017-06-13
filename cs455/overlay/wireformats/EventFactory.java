package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import cs455.overlay.node.Node;

public class EventFactory {
	
	private static EventFactory instance = null;
	private Node node = null; 
	
	private EventFactory(){};
	
	public static EventFactory getInstance(){
		// Creating Singleton instance
		if(instance == null){
			instance = new EventFactory();
		}
		return instance;
	}
	
	public void setNodeInstance(Node n)
	{
		node = n;
	}
	
	public void processReceivedMessage(byte[] data) throws IOException
	{
		int messageType = getMessageType(data);
		System.out.println("Message Type: " + messageType);
		
		if(messageType == Protocol.REGISTER_REQUEST)
		{
			Event e = new RegisterRequest();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.REGISTER_RESPONSE)
		{
			Event e = new RegisterResponse();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.DEREGISTER_REQUEST)
		{
			Event e = new DeregisterRequest();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.DEREGISTER_RESPONSE)
		{
			Event e = new DeregisterResponse();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.MESSAGING_NODES_LIST)
		{
			Event e = new MessagingNodesList();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.INCOMING_OVERLAY_CONNECTION)
		{
			System.out.println("Event Factory INCOMING_OVERLAY_CONNECTION");
			Event e = new IncomingOverlayConnection();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.CLIENT_LINK_INFO)
		{
			System.out.println("Event Factory CLIENT_LINK_INFO");
			Event e = new ClientLinkInfo();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.LINK_WEIGHTS)
		{
			System.out.println("Event Factory LINK_WEIGHTS");
			Event e = new LinkWeights();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.TASK_INITIATE)
		{
			System.out.println("Event Factory TASK_INITIATE");
			Event e = new TaskInitiate();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.MESSAGE)
		{
			//synchronized (this){
			System.out.println("Event Factory MESSAGE");
			Event e = new Message();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.TASK_COMPLETE)
		{
			System.out.println("Event Factory TASK_COMPLETE");
			Event e = new TaskComplete();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.PULL_TRAFFIC_SUMMARY)
		{
			System.out.println("Event Factory PULL_TRAFFIC_SUMMARY");
			Event e = new TaskSummaryRequest();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		else if(messageType == Protocol.TRAFFIC_SUMMARY)
		{
			System.out.println("Event Factory TRAFFIC_SUMMARY");
			Event e = new TaskSummaryResponse();
			e.getType(data);
			e.setMessageType(messageType);
			node.onEvent(e);
		}
		
	}
	
	private int getMessageType(byte[] data) throws IOException
	{
		int type;
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		type = din.readInt();
		baInputStream.close();
		din.close();
		return type;
	}
}
