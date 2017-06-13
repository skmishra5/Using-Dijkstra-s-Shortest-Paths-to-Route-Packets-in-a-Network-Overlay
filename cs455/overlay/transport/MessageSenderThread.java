package cs455.overlay.transport;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.TaskComplete;

public class MessageSenderThread implements Runnable{
	
	String m_IP;
	int m_port;
	int m_numOfRounds;
	int m_sendTracker;
	int m_sumOfSentMessages;
	ArrayList<String> m_uniqueNodeList;
	RoutingCache m_routingCache;
	HashMap<String, Socket> m_clientSocketStore;
	Socket m_registryClientSocket;
	Random random = new Random();
	Socket sendMessageSocket = null;
	private volatile boolean done = false;
	private boolean flag = false;
	
	public void setDone()
	{
		done = true;
	}
	
	public MessageSenderThread(String IP, int port, int numOfRounds, int sendTracker, int sumOfSentMessages, 
			ArrayList<String> uniqueNodeList, RoutingCache routingCache, 
			HashMap<String, Socket> clientSocketStore,Socket registryClientSocket)
	{
		this.m_IP = IP;
		this.m_port = port;
		this.m_numOfRounds = numOfRounds;
		this.m_sendTracker = sendTracker;
		this.m_sumOfSentMessages = sumOfSentMessages;
		this.m_uniqueNodeList = uniqueNodeList;
		this.m_routingCache = routingCache;
		this.m_clientSocketStore = clientSocketStore;
		this.m_registryClientSocket = registryClientSocket;
	}
	
	public int getSendTracker()
	{
		return m_sendTracker;
	}
	
	public int getSumOfSentMessages()
	{
		return m_sumOfSentMessages;
	}
	
	@Override
	public void run() {
		while(!done){
			
		if(flag != true){
		// TODO Auto-generated method stub
		for(int i = 0; i < m_numOfRounds; i++)
		{
			String destination = "";
			int nodeID = random.nextInt(m_uniqueNodeList.size() - 1) + 0;
			destination = m_uniqueNodeList.get(nodeID);
			System.out.println("Destination: " + destination);
			
			if(!destination.equals(m_IP + ":" + m_port))
			{
				HashMap<String, String> tempPath = m_routingCache.getShortestRoutingPath();
				String[] nearestLocation;
				if(tempPath.containsKey(destination))
				{
					nearestLocation = tempPath.get(destination).split(" ");
					sendMessageSocket = m_clientSocketStore.get(nearestLocation[0]);
				}
				
				TCPSender sender = null;
				try {
					sender = new TCPSender(sendMessageSocket);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				Message message= new Message();
				for(int j = 0; j < 5; j ++)
				{
					//int payload = random.nextInt(Integer.MAX_VALUE) + (Integer.MIN_VALUE);
					int payload = (int) random.nextLong();
					try {								
						byte[] dataToSend = message.constructMessage(m_IP + ":" + m_port, destination, payload);
						sender.sendData(dataToSend);
						m_sendTracker += 1;
						m_sumOfSentMessages += payload;
						System.out.println("Number of Messages Sent: " + m_sendTracker + " " + m_sumOfSentMessages);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				/*try {
					Thread.sleep(20);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				}
			}
			else
			{
				i = i - 1;
			}
		}
	//}
//}

		try {
			TCPSender sender =  new TCPSender(m_registryClientSocket);
			TaskComplete taskComplete = new TaskComplete();
			byte[] dataToSend = taskComplete.sendTaskComplete(m_IP, m_port);
			sender.sendData(dataToSend);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		flag = true;
		}}
		
	}

}
