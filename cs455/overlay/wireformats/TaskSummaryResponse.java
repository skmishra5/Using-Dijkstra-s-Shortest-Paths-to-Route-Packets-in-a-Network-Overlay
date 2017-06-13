package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class TaskSummaryResponse implements Event{
	private int type;
	private long timestamp;
	private String sourceIP;
	private int portNumber;
	private int numberOfMessagesSent;
	private int sumOfSentMessages;
	private int numberOfMessagesReceived;
	private int sumOfReceivedMessages;
	private int numberOfMessagesRelayed;

	public void setMessageType(int messageType){ type = messageType; }
	public int getMessageType(){ return type; }
	public String getIPAddress(){ return sourceIP; }
	public int getLocalPortNumber(){ return -1;}
	public int getListenPortNumber(){ return portNumber;}
	public byte getStatusCode(){ return 1; }
	public String getInfo(){ return null; }
	public String getPeerMessagingNode(){ return null; }
	public String getOwnIPPortInfo(){ return null; }
	public String getFirstIPPortInfo(){ return null; }
	public String getSecondIPPortInfo(){ return null; }
	public String getThirdIPPortInfo(){ return null; }
	public String getFourthIPPortInfo(){ return null; }
	public int getNumberOfRounds(){ return -1; }
	public int getPayload(){ return 0; }
	public String getMessageSourceAddress(){ return null; }
	public String getMessageDestinationAddress(){ return null; }
	public int getnumberOfMessagesSent(){ return numberOfMessagesSent;}
	public int getsumOfSentMessages(){ return sumOfSentMessages;}
	public int getnumberOfMessagesReceived(){ return numberOfMessagesReceived;}
	public int getsumOfReceivedMessages(){ return sumOfReceivedMessages;}
	public int getnumberOfMessagesRelayed(){ return numberOfMessagesRelayed;}
	
	
	public byte[] sendTrafficSummaryResponse(String srcIP, int port, int numSent, int sumOfSent, int numRecvd, int sumOfRecvd, 
						int numRelayed) throws IOException
	{
		type = Protocol.TRAFFIC_SUMMARY;
		Date dte=new Date();
	    timestamp = dte.getTime();
	    sourceIP = srcIP;
	    portNumber = port;
	    numberOfMessagesSent = numSent;
	    sumOfSentMessages = sumOfSent;
	    numberOfMessagesReceived = numRecvd;
	    sumOfReceivedMessages = sumOfRecvd;
	    numberOfMessagesRelayed = numRelayed;
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
		sourceIP = new String(identifierBytes);
		
		portNumber = din.readInt();
		numberOfMessagesSent = din.readInt();
		sumOfSentMessages = din.readInt();
		numberOfMessagesReceived = din.readInt();
		sumOfReceivedMessages = din.readInt();
		numberOfMessagesRelayed = din.readInt();
		
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
		
		byte[] identifierBytes = sourceIP.getBytes();
		int elementLength = identifierBytes.length;
		dout.writeInt(elementLength);
		dout.write(identifierBytes);

		dout.writeInt(portNumber);
		dout.writeInt(numberOfMessagesSent);
		dout.writeInt(sumOfSentMessages);
		dout.writeInt(numberOfMessagesReceived);
		dout.writeInt(sumOfReceivedMessages);
		dout.writeInt(numberOfMessagesRelayed);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
}
