package cs455.overlay.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class StatisticsCollectorAndDisplay {
	
	private HashMap<String, String> statisticsStore = new HashMap<String, String>();
	
	public synchronized void statisticsCollector(String node, int numSent, int numRcvd, int sumOfSent, int sumOfRcvd, 
			int numRelayed)
	{
		statisticsStore.put(node, numSent + " " + numRcvd + " " + sumOfSent + " " + sumOfRcvd + " " + numRelayed);
	}
	
	public void statisticsDisplay()
	{
		int numSent = 0, numRcvd = 0, sumSent = 0, sumRcvd = 0; 
		Set set = statisticsStore.entrySet();
		Iterator iterator = set.iterator();
		System.out.println("Node Number" + "\t" + "Num Msgs Sent" + "\t" + "Num Msgs Rcvd" + "\t"
				+ "Sum of sent msgs" + "\t" + "Sum of rcvd msgs" + "\t" + "Num of msgs relayed");
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
			
			String[] token = mentry.getValue().toString().split(" ");
			numSent += Integer.parseInt(token[0]);
			numRcvd += Integer.parseInt(token[1]);
			sumSent += Integer.parseInt(token[2]);
			sumRcvd += Integer.parseInt(token[3]);
			
			System.out.print("Node " + mentry.getKey());
			if(Integer.parseInt(token[3]) == 0)
			{
				System.out.println("\t\t" + token[0] + "\t\t" + token[1] + "\t\t" + token[2] + "\t\t" + token[3] + "\t\t\t" + token[4]);
			}
			else
			{
				System.out.println("\t\t" + token[0] + "\t\t" + token[1] + "\t\t" + token[2] + "\t\t" + token[3] + "\t\t" + token[4]);
			}
		}
		System.out.println("Sum" + "\t\t" + numSent + "\t\t" + numRcvd + "\t\t" + sumSent + "\t\t" + sumRcvd);
	}
	
}
