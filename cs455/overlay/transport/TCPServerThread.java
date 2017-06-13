package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.util.LoggerModule;
import cs455.overlay.wireformats.EventFactory;

public class TCPServerThread implements Runnable{

	private int m_portNumber = -1;
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private LoggerModule logm = null;
	private Logger logger = null;
	private String m_node = "";
	private EventFactory m_ef;
	private int ownServerPort = -1;
	private HashMap<String, Socket> clientSocketInfo = new HashMap<String, Socket>();
	private volatile boolean done = false;
	private TCPReceiverThread receiver;
	private Thread receiverThread;
	private MessagingNode msgNode = new MessagingNode();
	
	public void setDone()
	{
		/*if(receiver != null)
		{
			// Stopping the receiver thread
			System.out.println("Hi1");
			receiver.setDone();
		}*/
		done = true;
	}
	
	
	public TCPServerThread(int portNumber, String node, EventFactory ef)
	{
		this.m_portNumber = portNumber;
		this.m_node = node;
		this.m_ef = ef;
		// Setting up logger	
		logm = new LoggerModule(TCPServerThread.class.getSimpleName());
		logger = logm.Logging();
	}
	
	public int getOwnPort()
	{
		ownServerPort = serverSocket.getLocalPort();
		return ownServerPort;
	}
	
	public ServerSocket getServerSocket()
	{
		return serverSocket;
	}
	
	public Socket getClientSocket(String IPPortInfo)
	{
		Socket tempClientSocket = null;
		
		if(clientSocketInfo.containsKey(IPPortInfo))
		{
			tempClientSocket = clientSocketInfo.get(IPPortInfo);
		}
		return tempClientSocket;
	}
	
	public void removeClientSocketInfo(String IPPortInfo)
	{
		if(clientSocketInfo.containsKey(IPPortInfo))
		{
			clientSocketInfo.remove(IPPortInfo);
		}
	}

	@Override
	public void run() {
		try{
			serverSocket = new ServerSocket(m_portNumber); // binding socket to port
			//serverSocket.setReceiveBufferSize(63999);
		}
		catch(IOException e){
			e.printStackTrace();
			System.out.println("Server error");
		}
		
		while(!done){
			while(!serverSocket.isClosed())  //accepting the client infinitely
			{
				logger.log(Level.INFO, m_node +"Server Started on Port Number " + getOwnPort());
				try {
					clientSocket = serverSocket.accept();
					//clientSocket.setReceiveBufferSize(43800);
					synchronized (this){
						clientSocketInfo.put(clientSocket.getInetAddress().toString().split("/")[1] + ":" + clientSocket.getPort(), clientSocket);
					}
					msgNode.storeIncomingConnection(clientSocket.getInetAddress().toString().split("/")[1] + ":" + clientSocket.getPort(), clientSocket);
					receiver = new TCPReceiverThread(clientSocket);
					receiverThread = new Thread(receiver);
					receiverThread.start();
					receiver.setEventFactoryInstance(m_ef);
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					System.out.println("-Server Socket closed-");
					break;
				}

			}
		}
		
	}
}
