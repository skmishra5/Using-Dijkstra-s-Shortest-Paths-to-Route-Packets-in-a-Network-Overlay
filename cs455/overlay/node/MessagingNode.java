package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cs455.overlay.transport.CommandThread;
import cs455.overlay.transport.MessageSenderThread;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.LoggerModule;
import cs455.overlay.wireformats.ClientLinkInfo;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.IncomingOverlayConnection;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegisterRequest;
import cs455.overlay.wireformats.TaskComplete;
import cs455.overlay.wireformats.TaskSummaryResponse;
import cs455.overlay.dijkstra.Link;
import cs455.overlay.dijkstra.OverlayGraph;
import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.dijkstra.ShortestPath;
import cs455.overlay.dijkstra.Vertex;

public class MessagingNode implements Node{

	private static LoggerModule logm = null;
	private static Logger logger = null;
	private static String ownIP = "";
	private InetAddress registryHost = null;
	private int registryport = -1;
	private EventFactory eventFactory;
	private static int ownServerPort = -1;
	private int localPort = -1;
	private TCPServerThread server;
	private CommandThread commandInput;
	private Socket registryClientSocket = null;
	private Thread commandThread;
	private Thread serverThread;
	private TCPReceiverThread clientReceiver = null;
	private Thread clientreceiverThread;
	private RoutingCache routingCache;
	private Random random = new Random();
	private int sendTracker = 0;
	private int receiveTracker = 0;
	private int sumOfSentMessages = 0;
	private int sumOfReceivedMessages = 0;
	private int numMessagesRelayed = 0;
	private MessageSenderThread messageSender;
	// A hash map to store client-socket references
	private static HashMap<String, Socket> m_clientSocketStore = new HashMap<String, Socket>();
	private static HashMap<String, Socket> m_tempClientSocketStore = new HashMap<String, Socket>();
	private static HashMap<String, Integer> m_linkWeightsStore = new HashMap<String, Integer>();
	private HashSet<String> uniqueVertices; // Store all the node information in the overlay
	
	public void Initialize(String[] args)
	{
		// Setting up logger	
		logm = new LoggerModule(Registry.class.getSimpleName());
		logger = logm.Logging();
		
		// Checking number of command line arguments which is one
		if(args.length != 2)
		{
			logger.log(Level.WARNING, "Enter Registry IP and Port Number");
			return;
		}
		
		logger.log(Level.INFO, "Initializing the Messaging Node");
		
		// Getting the Registry IP and port number from the command line
		try {
			registryHost = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		registryport = Integer.parseInt(args[1]);
		try {
			ownIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		// Initializing Event Factory Singleton Instance
		eventFactory = EventFactory.getInstance();
		eventFactory.setNodeInstance(this);
		
		// Initializing the server thread to listen to other connections
		server = new TCPServerThread(0,  MessagingNode.class.getSimpleName(), eventFactory);
		serverThread = new Thread(server);
		serverThread.start();
		
		// Opening connection to the Registry node
		openRegistryConnection(registryHost, registryport);
		
		// Sending Register Message to Registry
		sendRegisterMessage(registryHost, registryport);
		
		commandInput = new CommandThread(registryClientSocket, ownIP, localPort, ownServerPort);
		Thread commandThread = new Thread(commandInput);
		commandThread.start();
		
	}

	// Opening connection to the Registry node
	private void openRegistryConnection(InetAddress host, int port)
	{
		
		//String IPPortKey = "";
		
		try {
			registryClientSocket = new Socket(host, port);
			localPort = registryClientSocket.getLocalPort();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Opening receiver thread on the same client socket to receive data
		
		try {
			clientReceiver = new TCPReceiverThread(registryClientSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clientreceiverThread = new Thread(clientReceiver);
		clientreceiverThread.start();
		clientReceiver.setEventFactoryInstance(eventFactory);
		
		//IPPortKey = host + ":" + port;
		//m_clientSocketStore.put(IPPortKey, clientSocket);
		
	}
	
	// Sending Register Message to Registry
	private void sendRegisterMessage(InetAddress host, int port)
	{
		//String IPPortKey = host + ":" + port;
		//Socket registryClientSocket = m_clientSocketStore.get(IPPortKey);
		
		try {
			TCPSender sender =  new TCPSender(registryClientSocket);
			RegisterRequest regReq= new RegisterRequest();
			ownServerPort = server.getOwnPort();
			byte[] dataToSend = regReq.registerRequestMessage(ownIP, localPort, ownServerPort);
			sender.sendData(dataToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEvent(Event e) {
		
		int messageType = e.getMessageType();
		logger.log(Level.INFO, "OnEvent messageType " + messageType);
		
		if(messageType == Protocol.REGISTER_RESPONSE)
		{
			logger.log(Level.INFO, e.getStatusCode() + ":" + e.getInfo());
		}
		else if(messageType == Protocol.DEREGISTER_RESPONSE)
		{
			logger.log(Level.INFO, e.getStatusCode() + ":" + e.getInfo());
			if(e.getStatusCode() == Protocol.SUCCESS)
			{
				// Closing the command thread
				if(commandThread != null)
				{
					commandInput.setDone();
				}
				
				
				if(clientreceiverThread != null)
				{
					clientReceiver.setDone();
				}
				
				// Closing the registry socket
				if(registryClientSocket != null)
				{
					try {
						registryClientSocket.close();
						registryClientSocket = null;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				// Closing its own listening and receiver thread
				if(serverThread != null)
				{
					server.setDone();
				}
				
				ServerSocket tempServerSocket = server.getServerSocket();
				
				if(tempServerSocket != null)
				{
					// Closing the listening socket
					try {
						if(tempServerSocket != null){
							tempServerSocket.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		else if(messageType == Protocol.MESSAGING_NODES_LIST)
		{
			logger.log(Level.INFO, "Connecting to the Peer Nodes....");
			
			String nodeList = e.getPeerMessagingNode();
			String[] token = nodeList.split(",");
			
			try {
				openPeerConnection(InetAddress.getByName(token[0].split(":")[0]), Integer.parseInt(token[0].split(":")[1]));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				openPeerConnection(InetAddress.getByName(token[1].split(":")[0]), Integer.parseInt(token[1].split(":")[1]));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if(messageType == Protocol.INCOMING_OVERLAY_CONNECTION)
		{
			logger.log(Level.INFO, "INCOMING_OVERLAY_CONNECTION message received.");
			
			String host = e.getIPAddress();
			int localPort = e.getLocalPortNumber();
			int listenPort = e.getListenPortNumber();
			
			String IPPortInfo = host + ":" + localPort;
			
			if(m_tempClientSocketStore.containsKey(IPPortInfo))
			{
				Socket tempSocket = m_tempClientSocketStore.get(IPPortInfo);
				m_clientSocketStore.put(host + ":" + listenPort, tempSocket);
			}
			
			if(m_clientSocketStore.size() == 4)
			{
				logger.log(Level.INFO, "All connections are established. Number of connections: " + m_clientSocketStore.size());
				Set set = m_clientSocketStore.entrySet();
				Iterator iterator = set.iterator();
				String zero, one, two, three, four, allInfo = "";
				while(iterator.hasNext()) {
					Map.Entry mentry = (Map.Entry)iterator.next();
					logger.log(Level.INFO, "key is: "+ mentry.getKey() + " & Value is: ");
					System.out.println(mentry.getValue());
					allInfo += mentry.getKey() + ",";
				}
				one = allInfo.split(",")[0];
				two = allInfo.split(",")[1];
				three = allInfo.split(",")[2];
				four = allInfo.split(",")[3];
				zero = ownIP + ":" + ownServerPort;
				try {
					TCPSender sender =  new TCPSender(registryClientSocket);
					ClientLinkInfo cliLinkInfo= new ClientLinkInfo();
					byte[] dataToSend = cliLinkInfo.incomingOverlayConnRequest(zero, one, two, three, four);
					sender.sendData(dataToSend);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		    
		}
		else if(messageType == Protocol.LINK_WEIGHTS)
		{
			logger.log(Level.INFO, "Link Weights for all the Links are: " + e.getInfo());
			
			Vertex vertex1 = null;
			Vertex vertex2 = null;
			Vertex altVertex1 = null;
			Vertex altVertex2 = null;
			Vertex uniVertex;
			Link link1;
			Link link2;

			ArrayList<Vertex> listOfVertices = new ArrayList<Vertex>();
			ArrayList<Link> listOfLinks = new ArrayList<Link>();
			uniqueVertices = new HashSet<String>(); 
			
			String[] token = e.getInfo().split("\n");
			for(int i = 0; i < token.length; i++)
			{
				m_linkWeightsStore.put(token[i].toString().split(" ")[0] + " " + token[i].toString().split(" ")[1],
						Integer.parseInt(token[i].toString().split(" ")[2]));
			}
			
			Set set = m_linkWeightsStore.entrySet();
			Iterator iterator = set.iterator();
			while(iterator.hasNext()) {
				Map.Entry mentry = (Map.Entry)iterator.next();
				//logger.log(Level.INFO, "key is: "+ mentry.getKey() + " & Value is: ");
				//System.out.println(mentry.getValue());
				
				String[] nodes = mentry.getKey().toString().split(" ");
				
				uniqueVertices.add(nodes[0]);
				uniqueVertices.add(nodes[1]);
			}
			
			for(String s : uniqueVertices)
			{
				if(s.equals(ownIP + ":" + ownServerPort))
				{
					uniVertex = new Vertex(s, s);
					listOfVertices.add(uniVertex);
				}
			}
			
			for(String s : uniqueVertices)
			{
				if(!s.equals(ownIP + ":" + ownServerPort))
				{
					uniVertex = new Vertex(s, s);
					listOfVertices.add(uniVertex);
				}
			}
			
			Set set1 = m_linkWeightsStore.entrySet();
			Iterator iterator1 = set1.iterator();
			while(iterator1.hasNext()) {
				Map.Entry mentry1 = (Map.Entry)iterator1.next();
				//logger.log(Level.INFO, "key is: "+ mentry.getKey() + " & Value is: ");
				//System.out.println(mentry.getValue());
				
				String[] nodes = mentry1.getKey().toString().split(" ");
				
				for(Vertex vertex : listOfVertices)
				{
					if(vertex.getNodeName().equals(nodes[0].trim()))
					{
						vertex1 = vertex;
						altVertex2 = vertex;
					}
					
					if(vertex.getNodeName().equals(nodes[1].trim()))
					{
						vertex2 = vertex;
						altVertex1 = vertex;
					}
				}
				
				/*for(Vertex vertex : listOfVertices)
				{
					if(vertex.getNodeName().equals(nodes[0].trim()))
					{
						vertex1 = vertex;
					}
					
					if(vertex.getNodeName().equals(nodes[1].trim()))
					{
						vertex2 = vertex;
					}
				}
				
				if(nodes[1].trim().equals(ownIP + ":" + ownServerPort))
				{
					for(Vertex vertex : listOfVertices)
					{
						if(vertex.getNodeName().equals(ownIP + ":" + ownServerPort))
						{
							vertex1 = vertex;
						}
						
						if(vertex.getNodeName().equals(nodes[0].trim()))
						{
							vertex2 = vertex;
						}
					}
				}*/
			
				link1 = new Link(vertex1, vertex2, Integer.parseInt(mentry1.getValue().toString()));
				link2= new Link(altVertex1, altVertex2, Integer.parseInt(mentry1.getValue().toString()));
				listOfLinks.add(link1);
				listOfLinks.add(link2);
			}
			
			for(Vertex vertex : listOfVertices)
			{
				System.out.println("Messaging Node Vertex: " + vertex.getNodeName());
			}
			
			for(Link l : listOfLinks)
			{
				System.out.println("Messaging Node Link: " + l.getSourceNode().getNodeName() + " " + l.getDestinationNode().getNodeName() + " " + l.getLinkWeight());
			}
						
			routingCache = new RoutingCache(ownIP + ":" + ownServerPort, listOfVertices, listOfLinks);
			routingCache.storeRoutingInformation();
		}
		else if(messageType == Protocol.TASK_INITIATE)
		{
			logger.log(Level.INFO, "Task Initiate Message Received with number of rounds: " + e.getNumberOfRounds());
			
			int numOfRounds = e.getNumberOfRounds();
			Socket sendMessageSocket = null;
			sendTracker = 0;
			receiveTracker = 0;
			sumOfSentMessages = 0;
			sumOfReceivedMessages = 0;
			numMessagesRelayed = 0;
			
			ArrayList<String> uniqueNodeList = new ArrayList<String>(uniqueVertices);
			
			//messageSender = new MessageSenderThread(ownIP, ownServerPort, numOfRounds, sendTracker,
				//	sumOfSentMessages, uniqueNodeList, routingCache, m_clientSocketStore, registryClientSocket);
			//Thread messageThread = new Thread(messageSender);
			//messageThread.start();
			
			/*for(String destination: uniqueVertices)
			{
				if(!destination.equals(ownIP + ":" + ownServerPort))
				{
					HashMap<String, String> tempPath = routingCache.getShortestRoutingPath();
					String[] nearestLocation;
					if(tempPath.containsKey(destination))
					{
						nearestLocation = tempPath.get(destination).split(" ");
						sendMessageSocket = m_clientSocketStore.get(nearestLocation[0]);
					}
				*/	
					for(int i = 0; i < numOfRounds; i++)
					{
						String destination = "";
						int nodeID = random.nextInt(uniqueNodeList.size() - 1) + 0;
						destination = uniqueNodeList.get(nodeID);
						//System.out.println("Destination: " + destination);
						//logger.log(Level.INFO, "Destination: " + destination);
						
						if(!destination.equals(ownIP + ":" + ownServerPort))
						{
							HashMap<String, String> tempPath = routingCache.getShortestRoutingPath();
							String[] nearestLocation;
							if(tempPath.containsKey(destination))
							{
								nearestLocation = tempPath.get(destination).split(" ");
								sendMessageSocket = m_clientSocketStore.get(nearestLocation[0]);
							}
							
							//logger.log(Level.INFO, "Sending Messages...");
							
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
									byte[] dataToSend = message.constructMessage(ownIP + ":" + ownServerPort, destination, payload);
									sender.sendData(dataToSend);
									synchronized (this){
										sendTracker += 1;
										sumOfSentMessages += payload;}
									//System.out.println("Number of Messages Sent: " + sendTracker + " " + sumOfSentMessages);
									//logger.log(Level.INFO, "Number of Messages Sent: " + sendTracker);
									//logger.log(Level.INFO, "Sum of Messages Sent: " + sumOfSentMessages);
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
				TCPSender sender =  new TCPSender(registryClientSocket);
				TaskComplete taskComplete = new TaskComplete();
				byte[] dataToSend = taskComplete.sendTaskComplete(ownIP, ownServerPort);
				sender.sendData(dataToSend);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		else if(messageType == Protocol.MESSAGE)
		{

			//logger.log(Level.INFO, "Message Received From: " + e.getMessageSourceAddress() + " For: " 
				//	+ e.getMessageDestinationAddress());
			
			String sourceAddress = e.getMessageSourceAddress();
			String destinationAddress = e.getMessageDestinationAddress();
			int payload = e.getPayload();
			Socket sendMessageSocket = null;
			
			if(destinationAddress.equals(ownIP + ":" + ownServerPort))
			{
				synchronized (this){
					receiveTracker += 1;
					sumOfReceivedMessages += payload;
				}
					//logger.log(Level.INFO, "Message reached its destination. " + receiveTracker + " " + sumOfReceivedMessages);
				//}
				//System.out.println("Message reached its destination.");
			}
			else
			{
				HashMap<String, String> tempPath = routingCache.getShortestRoutingPath();
				String[] nearestLocation;
				if(tempPath.containsKey(destinationAddress))
				{
					nearestLocation = tempPath.get(destinationAddress).split(" ");
					sendMessageSocket = m_clientSocketStore.get(nearestLocation[0]);
				}
				
				try {
					TCPSender sender =  new TCPSender(sendMessageSocket);
					Message message= new Message();
					byte[] dataToSend = message.constructMessage(sourceAddress, destinationAddress, payload);
					sender.sendData(dataToSend);
					synchronized (this){
						numMessagesRelayed += 1;
						//System.out.println("Messages relayed: " + numMessagesRelayed);
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
			}
		}
		else if(messageType == Protocol.PULL_TRAFFIC_SUMMARY)
		{
			logger.log(Level.INFO, "Traffice Summary Request Arrived.");
			//int tempSendTracker = messageSender.getSendTracker();
			//int tempsumOfSentMessages = messageSender.getSumOfSentMessages();
			
			try {
				TCPSender sender =  new TCPSender(registryClientSocket);
				TaskSummaryResponse taskSummaryResponse= new TaskSummaryResponse();
				byte[] dataToSend = taskSummaryResponse.sendTrafficSummaryResponse(ownIP, ownServerPort,
						sendTracker, sumOfSentMessages, receiveTracker, sumOfReceivedMessages, numMessagesRelayed);
				sender.sendData(dataToSend);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	// Opening connection to the Registry node
	public void openPeerConnection(InetAddress host, int port)
	{
		Socket peerClientSocket = null;
		TCPReceiverThread peerClientReceiver = null;
		Thread peerClientreceiverThread;
		String IPPortKey;
		int peerLocalPort = -1;
			
		try {
			peerClientSocket = new Socket(host, port);
			peerClientSocket.setSendBufferSize(63999);
			peerLocalPort = peerClientSocket.getLocalPort();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		// Opening receiver thread on the same client socket to receive data
			
		try {
			peerClientReceiver = new TCPReceiverThread(peerClientSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		peerClientreceiverThread = new Thread(peerClientReceiver);
		peerClientreceiverThread.start();
		peerClientReceiver.setEventFactoryInstance(eventFactory);
		
		IPPortKey = host.toString().split("/")[1] + ":" + port;
		m_clientSocketStore.put(IPPortKey, peerClientSocket);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Sending the listening port and IP
		try {
			TCPSender sender =  new TCPSender(peerClientSocket);
			IncomingOverlayConnection inOverlayconn= new IncomingOverlayConnection();
			ownServerPort = server.getOwnPort();
			byte[] dataToSend = inOverlayconn.incomingOverlayConnRequest(ownIP, peerLocalPort, ownServerPort);
			sender.sendData(dataToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
			
	}
	
	public void storeIncomingConnection(String IPPortKey, Socket incomingSockRef)
	{
		m_tempClientSocketStore.put(IPPortKey, incomingSockRef);
	}
	
	public void printShortestPath()
	{
		RoutingCache route = new RoutingCache(null, null, null);
		HashMap<String, String> tempPath = route.getShortestRoutingPath();
		String selfIPPort = ownIP + ":" + ownServerPort;
		Set set = tempPath.entrySet();
		Iterator iterator = set.iterator();
		Set set1 = m_linkWeightsStore.entrySet();
		Iterator iterator1 = set1.iterator();
		Set set2 = m_linkWeightsStore.entrySet();
		Iterator iterator2 = set2.iterator();
		
		String finalPath1 = "";
		int weight;
		
		while(iterator.hasNext()) {
			
			Map.Entry mentry = (Map.Entry)iterator.next();
			String[] individualPath = mentry.getValue().toString().split(" ");
			String first = selfIPPort + " " + individualPath[0];
			String firstAlt = individualPath[0] + " " + selfIPPort;
			weight = getPathWeight(first, firstAlt);
			String finalPath = "";
			String one = getHostNameAndPortCombination(selfIPPort);
			String two = getHostNameAndPortCombination(individualPath[0]);
			finalPath += one + "--" + weight + "--" + two;
			
			for(int i = 0; i < individualPath.length; i++)
			{
				if((i + 1) != individualPath.length){
					String straight = individualPath[i] + " " + individualPath[i+1];
					String altStraight = individualPath[i+1] + " " + individualPath[i];
					
					weight = getPathWeight(straight, altStraight);
					String three = getHostNameAndPortCombination(individualPath[i+1]);
					finalPath += "--" + weight + "--" + three;
				}
			}
			
			System.out.println(finalPath);		
		}
	}
	
	public String getHostNameAndPortCombination(String one)
	{
		String result = "";
		String hostname = "";
		
		String[] token = one.split(":");
		
		try {
			hostname = InetAddress.getByName(token[0]).getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//String[] token1 = hostname.toString().split(".");
		
		result = hostname + ":" + token[1];
		
		return result;
	}
	
	public int getPathWeight(String first, String altFirst)
	{
		int weight = 0;
		Set set1 = m_linkWeightsStore.entrySet();
		Iterator iterator1 = set1.iterator();
		
		while(iterator1.hasNext()) {
			Map.Entry mentry1 = (Map.Entry)iterator1.next();
			String key = mentry1.getKey().toString();
			//System.out.println("Key in first while loop" + key);
			if((first.trim().contains(key.trim())) || (altFirst.trim().contains(key.trim())))
			{
				weight = Integer.parseInt(mentry1.getValue().toString());
			}
			
		}
		
		return weight;
	}
	
	public static void main(String[] args) {
		MessagingNode messagingNode = new MessagingNode();
		messagingNode.Initialize(args);
	}

}
