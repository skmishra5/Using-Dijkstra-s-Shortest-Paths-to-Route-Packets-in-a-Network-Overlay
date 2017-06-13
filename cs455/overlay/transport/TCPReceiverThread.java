package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cs455.overlay.node.Node;
import cs455.overlay.util.LoggerModule;
import cs455.overlay.wireformats.EventFactory;

public class TCPReceiverThread implements Runnable{

	private Socket m_socket;
	private DataInputStream din;
	//private LoggerModule logm = null;
	//private Logger logger = null;
	private Node node;
	private EventFactory eventFactory;
	private volatile boolean done = false;
	
	public void setDone()
	{
		done = true;
	}
	
	
	
	public TCPReceiverThread(Socket socket) throws IOException {
		this.m_socket = socket;
		m_socket.setReceiveBufferSize(63999);
		if(m_socket != null){
			//din = new DataInputStream(m_socket.getInputStream());	
			//logm = new LoggerModule(TCPReceiverThread.class.getSimpleName());
			//logger = logm.Logging();
		}
	}
	
	public void setEventFactoryInstance(EventFactory ef)
	{
		eventFactory = ef;
	}
	
	@Override
	public void run() {
//		while(!done){
			int dataLength;
			while (m_socket != null) {
				try {
					din = new DataInputStream(m_socket.getInputStream());
					dataLength = din.readInt();
					if(dataLength == -1){
						break;
					}
					byte[] data = new byte[dataLength];
					din.readFully(data, 0, dataLength);
					// Process received event
					eventFactory.processReceivedMessage(data); 
				}
				catch (SocketException se) {
					//logger.log(Level.WARNING, se.getMessage());
					break;
				}
				catch (IOException ioe) {
					//logger.log(Level.WARNING, ioe.getMessage()) ;
					break;
				}
//			}
		}
	}

}
