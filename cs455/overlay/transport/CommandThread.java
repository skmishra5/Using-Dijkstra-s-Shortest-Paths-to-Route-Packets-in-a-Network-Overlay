package cs455.overlay.transport;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.RegisterRequest;

public class CommandThread implements Runnable{

	private Scanner scanner = new Scanner(System.in);
	private Socket m_registryClientSocket;
	private String m_ownIP;
	private int m_localPort;
	private int m_listenPort;
	private volatile boolean done = false;
	private Registry registry = new Registry();
	private MessagingNode msgNode =  new MessagingNode();
	
	public void setDone()
	{
		done = true;
	}
	
	public CommandThread(Socket registryClientSocket, String ownIP, int localPort, int listenPort)
	{
		m_registryClientSocket = registryClientSocket;
		m_ownIP = ownIP;
		m_localPort = localPort;
		m_listenPort = listenPort;
	}
	
	public CommandThread()
	{
		// Do nothing for now
	}
	
	@Override
	public void run() {
		while(!done){
				String command = scanner.nextLine();
				System.out.println("Command " + command);
				String[] token = command.split(" ");
				if(command.equals("exit-overlay"))
				{
					try {
						TCPSender sender =  new TCPSender(m_registryClientSocket);
						DeregisterRequest deregReq= new DeregisterRequest();
						byte[] dataToSend = deregReq.deregisterRequestMessage(m_ownIP, m_localPort, m_listenPort);
						sender.sendData(dataToSend);
					} catch (IOException e) {
						e.printStackTrace();
					}
					scanner.close();
					break;
				}
				else if(token[0].equals("setup-overlay"))
				{
					if(Integer.parseInt(token[1]) == 4)
					{
						registry.setupOverlay();
					}
					else
					{
						System.out.println("The number of connection for the overlay should be 4.");
					}
				}
				else if(command.equals("send-overlay-link-weights"))
				{
					registry.sendOverlayLinkWeights();
				}
				else if((token[0].equals("start")) && (token[1] != null))
				{
					int numberOfRounds  = Integer.parseInt(token[1]);
					registry.sendTaskInitiateMessage(numberOfRounds);
				}
				else if(command.equals("list-messaging nodes"))
				{
					registry.printMessagingNodes();
				}
				else if(command.equals("list-weights"))
				{
					registry.printLinkWeights();
				}
				else if(command.equals("print-shortest-path"))
				{
					msgNode.printShortestPath();
				}
		}
	}

}
