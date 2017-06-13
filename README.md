# Using Dijkstra’s Shortest Paths to Route Packets in a Network Overlay

Instructions to execute the Project
-----------------------------------
-----------------------------------

1. The Makefile is kept at the "cs455/src" directory.

2. Please run "make clean" and "make all" in order to build the project.

3. Start the server on any host with the command "java cs455.overlay.node.Registry <port_number>".

4. Edit the file called "machine_list" which is inside the same directory in order to change the host name.

5. Run the "start.sh" script in order to start the messaging node. This script is kept at the same path.

6. Once the messaging nodes have been connected, you can run "setup-overlay <num_conn>". 

7. Then wait for 2-4 seconds, so that messaging node can acknowledge for the overlay formation and Registry can update its data structure.

8. Then the "send-overlay-link-weights" command can be fired.

9. The "start <num_rounds>" command could be used to start messaging after that.



File Description
----------------
----------------

Package "cs455.overlay.dijkstra"
--------------------------------

1. Link.java: This class file contains all the information about the links in the overlay.

2. OverlayGraph.java: This class file stores a list of vertices and links. Getter methods can be used to get these lists.

3. RoutingCache.java: This class file invokes the shortest path calculation method which is inside ShortestPath.java
		      and stores the shortest path in a data structure of every other node for a given node.

4. ShortestPath.java: This class file calculates the shortest path.

5. Vertex.java: This class file contains all the information about the vertices in the overlay.


Package "cs455.overlay.node"
---------------------------- 

1. MessagingNode.java: This class implements the "Node" interface and implements the onEvent method.
		       This also handles different commands and sending and receiving functionalities of messaging nodes in the overlay.

2. Node.java: This is an interface which has the onEvent method.

3. Registry.java: This class implements the "Node" interface and implements the onEvent method.
		  This also handles different commands and sending and receiving functionalities of registry node in the overlay.

Package "cs455.overlay.transport"
---------------------------------

1. CommandThread.java: This is thread which handles all the commands. This thread is started by both registry and messaging nodes.

2. TCPReceiverThread.java: This thread is started by both registry and messaging nodes. This handles the receipt of data on the socket.

3. TCPSender.java: This class handles the sending of data on the socket for both registry and messaging nodes.

4. TCPServerThread.java: This thread is started by both registry and messaging nodes in order to wait in listening mode.


Package "cs455.overlay.util"
----------------------------

1. LoggerModule.java: This initializes the logger module.

2. OverlayCreator.java: This creates the overlay network.

3. StatisticsCollectorAndDisplay.java: This collects all the statistics of the netowrk traffic and displays it at the registry.


Package "cs455.overlay.wireformats"
-----------------------------------

1. ClientLinkInfo.java: This message class in used by the messaging nodes to update the overlay information.

2. DeregisterRequest.java: This handles the deregister request of messaging nodes.

3. DeregisterResponse.java: This message is sent by the registry to messaging nodes if a de-registration is successful.

4. Event.java: All the message classes implements this interaface and implements its methods.

5. EventFactory.java: This is a singleton instance and contain the node information. All the received bytes are handled in this class.

6. IncomingOverlayConnection.java: This message is used by messaging nodes to keep track of incoming overlay connections.

7. LinkWeights.java: This message is sent by registry to all the messaging nodes with the information of all the links and their corressponding weights.

8. Message.java: This message is used to start messaging between messaging nodes.

9. MessagingNodesList.java: This message is sent by the registry to all the messaging nodes. This contains the information about nodes which should be 
			    connected by other node to form the overlay.

10. Protocol.java: This class contains the type of all the messages.

11. RegisterRequest.java: This message is sent by all the messaging nodes when they first start up.

12. RegisterResponse.java: This message is responded by the registry once the messaging node registration is successful.

13. TaskComplete.java: This message is sent by messaging nodes to the registry once sending of messages to other nodes are completed.

14. TaskInitiate.java: This message is sent by the registry to all the messaging nodes to start sending messages in the overlay.

15. TaskSummaryRequest.java: This message is sent by registry to all the messaging nodes once all the task complete messages are received.
			     This message requests for the traffice summary for particular number of rounds.

16. TaskSummaryResponse.java: This response message is sent by the messaging nodes to the registry once the TaskSummaryRequest message has arrived.
			      This message contains all the information about the traffic for a particular number of rounds.

