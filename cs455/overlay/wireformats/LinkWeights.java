package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class LinkWeights implements Event{
	private int type;
	private long timestamp;
	private int numberOfLinks;
	private String linkInformation;

	
	public void setMessageType(int messageType){ type = messageType; }	
	public int getMessageType(){ return type; }
	public String getIPAddress(){ return null; }
	public int getLocalPortNumber(){ return -1; }
	public int getListenPortNumber(){ return -1; }
	public byte getStatusCode(){ return 0 ; }
	public String getInfo(){ return linkInformation; }
	public String getPeerMessagingNode(){ return null; }
	public String getOwnIPPortInfo(){ return null; }
	public String getFirstIPPortInfo(){ return null; }
	public String getSecondIPPortInfo(){ return null; }
	public String getThirdIPPortInfo(){ return null; }
	public String getFourthIPPortInfo(){ return null; }
	public int getNumberOfRounds(){ return -1; }
	public int getPayload(){ return -1; }
	public String getMessageSourceAddress(){ return null; }
	public String getMessageDestinationAddress(){ return null; }
	public int getnumberOfMessagesSent(){ return -1;}
	public int getsumOfSentMessages(){ return -1;}
	public int getnumberOfMessagesReceived(){ return -1;}
	public int getsumOfReceivedMessages(){ return -1;}
	public int getnumberOfMessagesRelayed(){ return -1;}
	
	
	public byte[] sendLinkWeights(int numLinks, String info) throws IOException
	{
		type = Protocol.LINK_WEIGHTS;
		Date dte=new Date();
	    timestamp = dte.getTime();
	    numberOfLinks = numLinks;
		linkInformation = info;
		byte[] marshalledBytes = getBytes();
		return marshalledBytes;
	}
	
	@Override
	public void getType(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		type = din.readInt();
		timestamp = din.readLong();
		numberOfLinks = din.readInt();

		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		linkInformation = new String(identifierBytes);

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
		dout.writeInt(numberOfLinks);

		byte[] identifierBytes = linkInformation.getBytes();
		int elementLength = identifierBytes.length;
		dout.writeInt(elementLength);
		dout.write(identifierBytes);

		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
}
