package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class ClientLinkInfo implements Event{
	private int type;
	private long timestamp;
	private String m_ownIPPort;
	private String m_firstIPPort;
	private String m_secondIPPort;
	private String m_thirdIPPort;
	private String m_fourthIPPort;

	public void setMessageType(int messageType){ type = messageType; }
	public int getMessageType(){ return type; }
	public String getIPAddress(){ return null; }
	public int getLocalPortNumber(){ return -1;}
	public int getListenPortNumber(){ return -1;}
	public byte getStatusCode(){ return 0; }
	public String getInfo(){ return null; }
	public String getPeerMessagingNode(){ return null; }
	public String getOwnIPPortInfo(){ return m_ownIPPort; }
	public String getFirstIPPortInfo(){ return m_firstIPPort; }
	public String getSecondIPPortInfo(){ return m_secondIPPort; }
	public String getThirdIPPortInfo(){ return m_thirdIPPort; }
	public String getFourthIPPortInfo(){ return m_fourthIPPort; }
	public int getNumberOfRounds(){ return -1; }
	public int getPayload(){ return -1; }
	public String getMessageSourceAddress(){ return null; }
	public String getMessageDestinationAddress(){ return null; }
	public int getnumberOfMessagesSent(){ return -1;}
	public int getsumOfSentMessages(){ return -1;}
	public int getnumberOfMessagesReceived(){ return -1;}
	public int getsumOfReceivedMessages(){ return -1;}
	public int getnumberOfMessagesRelayed(){ return -1;}
	
	
	public byte[] incomingOverlayConnRequest(String ownIPPort, String firstIPPort, String secondIPPort,
			String thirdIPPort, String fourthIPPort) throws IOException
	{
		type = Protocol.CLIENT_LINK_INFO;
		Date dte=new Date();
	    timestamp = dte.getTime();
	    m_ownIPPort = ownIPPort;
	    m_firstIPPort = firstIPPort;
	    m_secondIPPort = secondIPPort;
	    m_thirdIPPort = thirdIPPort;
	    m_fourthIPPort = fourthIPPort;
		byte[] marshalledBytes = getBytes();
		return marshalledBytes;
	}
	
	@Override
	public void getType(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		type = din.readInt();
		timestamp = din.readLong();

		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		m_ownIPPort = new String(identifierBytes);
		
		int identifierLength1 = din.readInt();
		byte[] identifierBytes1 = new byte[identifierLength1];
		din.readFully(identifierBytes1);
		m_firstIPPort = new String(identifierBytes1);
		
		int identifierLength2 = din.readInt();
		byte[] identifierBytes2 = new byte[identifierLength2];
		din.readFully(identifierBytes2);
		m_secondIPPort = new String(identifierBytes2);
		
		int identifierLength3 = din.readInt();
		byte[] identifierBytes3 = new byte[identifierLength3];
		din.readFully(identifierBytes3);
		m_thirdIPPort = new String(identifierBytes3);
		
		int identifierLength4 = din.readInt();
		byte[] identifierBytes4 = new byte[identifierLength4];
		din.readFully(identifierBytes4);
		m_fourthIPPort = new String(identifierBytes4);


		baInputStream.close();
		din.close();		
	}
	
	@Override
	public byte[] getBytes() throws IOException {		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeInt(type);
		dout.writeLong(timestamp);

		byte[] identifierBytes = m_ownIPPort.getBytes();
		int elementLength = identifierBytes.length;
		dout.writeInt(elementLength);
		dout.write(identifierBytes);
		
		byte[] identifierBytes1 = m_firstIPPort.getBytes();
		int elementLength1 = identifierBytes1.length;
		dout.writeInt(elementLength1);
		dout.write(identifierBytes1);
		
		byte[] identifierBytes2 = m_secondIPPort.getBytes();
		int elementLength2 = identifierBytes2.length;
		dout.writeInt(elementLength2);
		dout.write(identifierBytes2);
		
		byte[] identifierBytes3 = m_thirdIPPort.getBytes();
		int elementLength3 = identifierBytes3.length;
		dout.writeInt(elementLength3);
		dout.write(identifierBytes3);
		
		byte[] identifierBytes4 = m_fourthIPPort.getBytes();
		int elementLength4 = identifierBytes4.length;
		dout.writeInt(elementLength4);
		dout.write(identifierBytes4);

		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
}
