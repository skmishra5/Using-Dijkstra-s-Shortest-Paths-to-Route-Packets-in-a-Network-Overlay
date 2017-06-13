package cs455.overlay.util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import cs455.overlay.node.Registry;

public class LoggerModule {
	
	private static Logger logger = null;
	private FileHandler fh = null;
	private static String hostname = "";
	private String m_className = "";
	
	public LoggerModule(String className)
	{
		this.m_className = className;
		logger = Logger.getLogger(m_className);
	}
	
	public Logger Logging()
	{
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			fh = new FileHandler("../log/" + hostname + "_" + m_className+".log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return logger;
	}
}
