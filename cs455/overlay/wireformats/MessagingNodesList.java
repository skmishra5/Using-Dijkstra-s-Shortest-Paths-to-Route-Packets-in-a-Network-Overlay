package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class MessagingNodesList implements Event{
	private int type;
	private long timestamp;
	private String nodeA;
	private String nodeB;
	private int numPeerNodes;
	
	public void setMessageType(int messageType){ type = messageType; }
	public int getMessageType(){ return type; }
	public String getIPAddress(){ return null; }
	public int getLocalPortNumber(){ return -1; }
	public int getListenPortNumber(){ return -1; }
	public byte getStatusCode(){ return 0; }
	public String getInfo(){ return null; }
	public String getPeerMessagingNode(){ return nodeA + "," + nodeB; }
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

	public byte[] messagingNodesListMessage(String firstNode, String secondNode) throws IOException
	{
		type = Protocol.MESSAGING_NODES_LIST;
		Date dte=new Date();
	    timestamp = dte.getTime();
	    nodeA = firstNode;
	    nodeB = secondNode;
	    numPeerNodes = 2;
		byte[] marshalledBytes = getBytes();
		return marshalledBytes;
	}
	
	@Override
	public void getType(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		type = din.readInt();
		timestamp = din.readLong();
		
		numPeerNodes = din.readInt();

		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		nodeA = new String(identifierBytes);
		
		int identifierLength1 = din.readInt();
		byte[] identifierBytes1 = new byte[identifierLength1];
		din.readFully(identifierBytes1);
		nodeB = new String(identifierBytes1);

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
		
		dout.writeInt(numPeerNodes);

		byte[] identifierBytes = nodeA.getBytes();
		int elementLength = identifierBytes.length;
		dout.writeInt(elementLength);
		dout.write(identifierBytes);
		
		byte[] identifierBytes1 = nodeB.getBytes();
		int elementLength1 = identifierBytes1.length;
		dout.writeInt(elementLength1);
		dout.write(identifierBytes1);


		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
}
