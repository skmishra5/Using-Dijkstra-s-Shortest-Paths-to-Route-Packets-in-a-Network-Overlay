package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Protocol{

	// Declaring status code
	public static final byte SUCCESS = 0;
	public static final byte FAILURE = -1;
	
	// Declaring Message Types
	public static final int REGISTER_REQUEST = 0;
	public static final int REGISTER_RESPONSE = 1;
	public static final int DEREGISTER_REQUEST = 2;
	public static final int DEREGISTER_RESPONSE = 3;
	public static final int MESSAGING_NODES_LIST = 4;
	public static final int INCOMING_OVERLAY_CONNECTION = 5;
	public static final int CLIENT_LINK_INFO = 6;
	public static final int LINK_WEIGHTS = 7;
	public static final int TASK_INITIATE = 8;
	public static final int MESSAGE = 9;
	public static final int TASK_COMPLETE = 10;
	public static final int PULL_TRAFFIC_SUMMARY = 11;
	public static final int TRAFFIC_SUMMARY = 12;
	

}
