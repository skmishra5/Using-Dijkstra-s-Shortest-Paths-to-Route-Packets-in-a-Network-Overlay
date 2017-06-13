package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import cs455.overlay.util.LoggerModule;

public class TCPSender {
	
	private Socket m_socket;
	private DataOutputStream dout;
	//private LoggerModule logm = null;
	//private Logger logger = null;
	
	public TCPSender(Socket socket) throws IOException {
		this.m_socket = socket;
		m_socket.setSendBufferSize(1000000);
		//dout = new DataOutputStream(socket.getOutputStream());
		//logm = new LoggerModule(TCPReceiverThread.class.getSimpleName());
		//logger = logm.Logging();
	}
	
	public void sendData(byte[] dataToSend) throws IOException {
		//logger.log(Level.INFO, "Data sent");
		dout = new DataOutputStream(m_socket.getOutputStream());
		System.out.println("Data sent");
		int dataLength = dataToSend.length;
		synchronized (m_socket){
			dout.writeInt(dataLength);
			dout.write(dataToSend, 0, dataLength);
			dout.flush();
		}
	}
}
