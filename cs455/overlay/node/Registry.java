package cs455.overlay.node;

import cs455.overlay.transport.CommandThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.LoggerModule;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.DeregisterResponse;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegisterRequest;
import cs455.overlay.wireformats.RegisterResponse;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.TaskSummaryRequest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Registry implements Node{
	
	private static LoggerModule logm = null;
	private static Logger logger = null;
	private int portNumber = -1;
	private EventFactory eventFactory;
	// Storage to keep information of all messaging nodes
	private static ArrayList<String> messagingNodeInfo = new ArrayList<String>();
	private static TCPServerThread server = null;
	private OverlayCreator overlayCreator = null;
	private CommandThread commandInput;
	private static HashMap<String, String> linkInformation = new HashMap<String, String>();
	private static HashMap<String, Integer> linkWeightsStore = new HashMap<String, Integer>();
	private static ArrayList<String> taskCompletedNodes = new ArrayList<String>();
	private Random random = new Random();
	private StatisticsCollectorAndDisplay stat = new StatisticsCollectorAndDisplay();
	private int nodeCount = 0;
	
	public void Initialize(String[] args)
	{	
		// Setting up logger	
		logm = new LoggerModule(Registry.class.getSimpleName());
		logger = logm.Logging();

		// Checking number of command line arguments which is one
		if(args.length != 1)
		{
			logger.log(Level.WARNING, "Enter Port Number");
			return;
		}
		
		logger.log(Level.INFO, "Initializing the Registry Node");
		// Getting port number from the command line
		portNumber = Integer.parseInt(args[0]);
		
		// Initializing Event Factory Singleton Instance
		eventFactory = EventFactory.getInstance();
		eventFactory.setNodeInstance(this);
		
		// Initializing the server thread
		server = new TCPServerThread(portNumber,  Registry.class.getSimpleName(), eventFactory);
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		// Starting the command thread to handle commands
		commandInput = new CommandThread();
		Thread commandThread = new Thread(commandInput);
		commandThread.start();
		
	}

	public void setupOverlay()
	{
		System.out.println("Inside Registry's setup overlay");
		if(messagingNodeInfo.size() > 4){
		overlayCreator = new OverlayCreator(messagingNodeInfo);
		HashMap<String, String> tempOverlayInfo = new HashMap<String, String>();
		tempOverlayInfo = overlayCreator.createOverlay();
		
		Set set = tempOverlayInfo.entrySet();
	      Iterator iterator = set.iterator();
	      while(iterator.hasNext()) {
	         Map.Entry mentry = (Map.Entry)iterator.next();
	         
	         String uniqueID = (String) mentry.getKey();
	         System.out.println("uniqueID " + uniqueID);
	         
	         //server.display();
	         
	         Socket tempClientSocket = server.getClientSocket(uniqueID.toString().trim());
	         String value = (String) mentry.getValue();
	         String[] token = value.split(",");
	         
	         
	         TCPSender sender = null;
			 try {
				sender = new TCPSender(tempClientSocket);
			 }catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			 }
	         MessagingNodesList mesNodeList = new MessagingNodesList();
	         byte[] dataToSend = null;
			 try {
				dataToSend = mesNodeList.messagingNodesListMessage(token[0], token[1]);
			 } catch (IOException e) {
				e.printStackTrace();
			 }
			 try {
				sender.sendData(dataToSend);
			 } catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			 }

	      }}
		else{
			System.out.println("The number of messaging node should be greater than the number of connections.");
		}
	}
	
	public void sendOverlayLinkWeights()
	{
		Set set = linkInformation.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
			String tempKey =  mentry.getKey().toString();
			String[] tempValue = mentry.getValue().toString().split(",");
			String path1 = tempKey + " " + tempValue[0];
			String path1Alt = tempValue[0] + " " + tempKey;
			String path2 = tempKey + " " + tempValue[1];
			String path2Alt = tempValue[1] + " " + tempKey;
			String path3 = tempKey + " " + tempValue[2];
			String path3Alt = tempValue[2] + " " + tempKey;
			String path4 = tempKey + " " + tempValue[3];
			String path4Alt = tempValue[3] + " " + tempKey;
			
			if(!((linkWeightsStore.containsKey(path1)) || (linkWeightsStore.containsKey(path1Alt))))
			{
				int weight = random.nextInt(10) + 1;
				linkWeightsStore.put(path1, weight);
			}
			
			if(!((linkWeightsStore.containsKey(path2)) || (linkWeightsStore.containsKey(path2Alt))))
			{
				int weight = random.nextInt(10) + 1;
				linkWeightsStore.put(path2, weight);
			}
			
			if(!((linkWeightsStore.containsKey(path3)) || (linkWeightsStore.containsKey(path3Alt))))
			{
				int weight = random.nextInt(10) + 1;
				linkWeightsStore.put(path3, weight);
			}
			
			if(!((linkWeightsStore.containsKey(path4)) || (linkWeightsStore.containsKey(path4Alt))))
			{
				int weight = random.nextInt(10) + 1;
				linkWeightsStore.put(path4, weight);
			}	
		}
		
		String linkInfo = "";
		Set set1 = linkWeightsStore.entrySet();
		Iterator iterator1 = set1.iterator();
		while(iterator1.hasNext()) {
			Map.Entry mentry1 = (Map.Entry)iterator1.next();
			linkInfo +=  mentry1.getKey() + " " + mentry1.getValue() + "\n";
			System.out.println(linkInfo);
		}
		
		int numLinks = linkWeightsStore.size();
		
		for(int i = 0; i < messagingNodeInfo.size(); i++)
		{
			String[] token = messagingNodeInfo.get(i).split(":");
			String IPPortInfo = token[0] + ":" + token[1];
			Socket tempClientSocket = server.getClientSocket(IPPortInfo);
			
			TCPSender sender = null;
			try {
				sender = new TCPSender(tempClientSocket);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			LinkWeights linkWeights= new LinkWeights();
			byte[] dataToSend = null;
			
			try {
				dataToSend = linkWeights.sendLinkWeights(numLinks, linkInfo);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sender.sendData(dataToSend);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		
	}
	
	public void printLinkWeights()
	{
		String linkInfo = "";
		int count = 0;
		Set set1 = linkWeightsStore.entrySet();
		Iterator iterator1 = set1.iterator();
		while(iterator1.hasNext()) {
			count++;
			Map.Entry mentry1 = (Map.Entry)iterator1.next();
			
			String[] token = mentry1.getKey().toString().split(" ");
			String[] hostname1 = token[0].split(":");
			String[] hostname2 = token[1].split(":");
			
			try {
				linkInfo =  InetAddress.getByName(hostname1[0]).getHostName() + ":" + hostname1[1] + "\t" +
						InetAddress.getByName(hostname2[0]).getHostName() + ":" + hostname2[1] + "\t" + mentry1.getValue() + "\n";
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Link " + count + "  -->    " + linkInfo);
		}
	}
	
	public void sendTaskInitiateMessage(int numOfRounds)
	{
		for(int i = 0; i < messagingNodeInfo.size(); i++)
		{
			String[] token = messagingNodeInfo.get(i).split(":");
			String IPPortInfo = token[0] + ":" + token[1];
			Socket tempClientSocket = server.getClientSocket(IPPortInfo);
			
			TCPSender sender = null;
			try {
				sender = new TCPSender(tempClientSocket);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			TaskInitiate taskInitiate = new TaskInitiate();
			byte[] dataToSend = null;
			
			try {
				dataToSend = taskInitiate.taskInitiateMessage(numOfRounds);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sender.sendData(dataToSend);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void onEvent(Event e) {
		
		int messageType = e.getMessageType();
		
		if(messageType == Protocol.REGISTER_REQUEST)
		{
			logger.log(Level.INFO, "Registration Successful for IP " + e.getIPAddress() + " and port number " + e.getLocalPortNumber() + ":" + e.getListenPortNumber());
			String IPPortInfo = e.getIPAddress() + ":" + e.getLocalPortNumber();
			String storeIPPortInfo = e.getIPAddress() + ":" + e.getLocalPortNumber() + ":" + e.getListenPortNumber();
			Socket tempClientSocket = server.getClientSocket(IPPortInfo);
			
			if((!messagingNodeInfo.contains(storeIPPortInfo)) && (tempClientSocket != null))
			{
				TCPSender sender = null;
				try {
					sender = new TCPSender(tempClientSocket);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				RegisterResponse regRes= new RegisterResponse();
				int numNodesOverlay = messagingNodeInfo.size() + 1;
				String tempInfo = "Registration request successful. The number of messaging nodes currently constituting the overlay is ("
						+ numNodesOverlay + ")";
				byte[] dataToSend = null;
				try {
					dataToSend = regRes.registerResponseMessage(Protocol.SUCCESS, tempInfo);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					sender.sendData(dataToSend);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				messagingNodeInfo.add(storeIPPortInfo);
				
			}
			else if(messagingNodeInfo.contains(storeIPPortInfo))
			{
				TCPSender sender = null;
				try {
					sender = new TCPSender(tempClientSocket);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				RegisterResponse regRes= new RegisterResponse();
				String tempInfo = "Already registered or Mismatch in the IP sent";
				byte[] dataToSend = null;
				try {
					dataToSend = regRes.registerResponseMessage(Protocol.FAILURE, tempInfo);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					sender.sendData(dataToSend);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else
			{
				logger.log(Level.SEVERE, "Mismatch in the IP sent");
			}
		}
		else if(messageType == Protocol.DEREGISTER_REQUEST)
		{
			logger.log(Level.INFO, "Deregistration request for IP " + e.getIPAddress() + " and port number " 
					+ e.getLocalPortNumber() + ":" + e.getListenPortNumber());
			String IPPortInfo = e.getIPAddress() + ":" + e.getLocalPortNumber();
			String storeIPPortInfo = e.getIPAddress() + ":" + e.getLocalPortNumber() + ":" + e.getListenPortNumber();
			Socket tempClientSocket = server.getClientSocket(IPPortInfo);
			
			if((messagingNodeInfo.contains(storeIPPortInfo)) && (tempClientSocket != null))
			{
				TCPSender sender = null;
				try {
					sender = new TCPSender(tempClientSocket);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				DeregisterResponse deregRes= new DeregisterResponse();
				String tempInfo = "Deregistration Successful. You can leave the overlay network.";
				byte[] dataToSend = null;
				try {
					dataToSend = deregRes.deregisterResponseMessage(Protocol.SUCCESS, tempInfo);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					sender.sendData(dataToSend);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(!messagingNodeInfo.contains(storeIPPortInfo))
			{
				TCPSender sender = null;
				try {
					sender = new TCPSender(tempClientSocket);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				DeregisterResponse deregRes= new DeregisterResponse();
				String tempInfo = "Already deregistered or Mismatch in the IP sent";
				byte[] dataToSend = null;
				try {
					dataToSend = deregRes.deregisterResponseMessage(Protocol.FAILURE, tempInfo);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					sender.sendData(dataToSend);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else
			{
				logger.log(Level.SEVERE, "Mismatch in the IP sent");
			}
			
			// Removing entry of the client socket
			server.removeClientSocketInfo(IPPortInfo);
			
			// Removing entry of the node
			if(messagingNodeInfo.contains(storeIPPortInfo))
			{
				messagingNodeInfo.remove(storeIPPortInfo);
			}
						
			// Closing the client socket for the messaging node
			/*try {
				tempClientSocket.close();
				tempClientSocket = null;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			
		}
		else if(messageType == Protocol.CLIENT_LINK_INFO)
		{
			synchronized (this){
				linkInformation.put(e.getOwnIPPortInfo(), e.getFirstIPPortInfo() + "," + e.getSecondIPPortInfo() + "," +
						e.getThirdIPPortInfo() + "," + e.getFourthIPPortInfo());
				Set set = linkInformation.entrySet();
				Iterator iterator = set.iterator();
				while(iterator.hasNext()) {
					Map.Entry mentry = (Map.Entry)iterator.next();
					logger.log(Level.INFO, "key is: "+ mentry.getKey() + " & Value is: ");
					System.out.println(mentry.getValue());
				}
			}
			
		}
		else if(messageType == Protocol.TASK_COMPLETE)
		{
			synchronized (this){
				logger.log(Level.INFO, "Message Received From IP: " + e.getIPAddress() + " And Port Number: " 
						+ e.getListenPortNumber());
				taskCompletedNodes.add(e.getIPAddress() + ":" + e.getListenPortNumber());
			}
			
			if(messagingNodeInfo.size() == taskCompletedNodes.size())
			{
				logger.log(Level.INFO, "All the TASK_COMPLETE messages are received from all the nodes in the overlay.");
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				for(int i = 0; i < messagingNodeInfo.size(); i++)
				{
					String[] token = messagingNodeInfo.get(i).split(":");
					String IPPortInfo = token[0] + ":" + token[1];
					Socket tempClientSocket = server.getClientSocket(IPPortInfo);
					
					TCPSender sender = null;
					try {
						sender = new TCPSender(tempClientSocket);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					TaskSummaryRequest taskSummaryRequest = new TaskSummaryRequest();
					byte[] dataToSend = null;
					
					try {
						dataToSend = taskSummaryRequest.sendTrafficSummaryRequest();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						sender.sendData(dataToSend);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				taskCompletedNodes.clear();
			}
		}
		else if(messageType == Protocol.TRAFFIC_SUMMARY)
		{
			synchronized (this){
				nodeCount += 1;
				stat.statisticsCollector(String.valueOf(nodeCount), e.getnumberOfMessagesSent(), e.getnumberOfMessagesReceived(), 
						e.getsumOfSentMessages(), e.getsumOfReceivedMessages(), e.getnumberOfMessagesRelayed());
			
				System.out.println(nodeCount + ":" + messagingNodeInfo.size());
				if(nodeCount == messagingNodeInfo.size())
				{
					stat.statisticsDisplay();
					nodeCount = 0;
				}
			}
		}
	}
	
	public void printMessagingNodes()
	{
		int count = 0;
		for(int i = 0; i < messagingNodeInfo.size(); i++)
		{
			count += 1;
			String[] token = messagingNodeInfo.get(i).split(":");
			String IPPortInfo = token[0] + ":" + token[2];
			System.out.println("Node " + count + " -->    " + IPPortInfo);
		}
	}
	
	public static void main(String[] args) {
		
		Registry registry =  new Registry();
		
		// Initialize the Registry node
		registry.Initialize(args);
	}
}
