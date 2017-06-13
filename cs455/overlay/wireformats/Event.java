package cs455.overlay.wireformats;

import java.io.IOException;

public interface Event {
	
	// Methods to Marshall and Unmarshall messages
	public void getType(byte[] marshalledBytes)
		throws IOException;
	public byte[] getBytes()
		throws IOException;
	
	//Method to set and get Message Type
	public void setMessageType(int messageType);
	public int getMessageType();
	public String getIPAddress();
	public int getLocalPortNumber();
	public int getListenPortNumber();
	public byte getStatusCode();
	public String getInfo();
	public String getPeerMessagingNode();
	public String getOwnIPPortInfo();
	public String getFirstIPPortInfo();
	public String getSecondIPPortInfo();
	public String getThirdIPPortInfo();
	public String getFourthIPPortInfo();
	public int getNumberOfRounds();
	public int getPayload();
	public String getMessageSourceAddress();
	public String getMessageDestinationAddress();
	public int getnumberOfMessagesSent();
	public int getsumOfSentMessages();
	public int getnumberOfMessagesReceived();
	public int getsumOfReceivedMessages();
	public int getnumberOfMessagesRelayed();
}
