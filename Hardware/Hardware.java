package Hardware;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

import gnu.io.*;

public class Hardware {
	
	
	public ArrayList<String> getPorts() {
		
		//returns a list with all of the available commport names as strings
		
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> portNameList = new ArrayList<>();
		String portName;
		
		while(portList.hasMoreElements()) {
			portName = portList.nextElement().getName();
			portNameList.add(portName);
		}
		return portNameList;
	}
	
	public SerialPort setupPort(String s) throws PortInUseException, NoSuchPortException, UnsupportedCommOperationException{
		
		//Sets up a SerialPort given the PortIdentifier as a String e.g. "COM3"
		
		CommPortIdentifier pID = CommPortIdentifier.getPortIdentifier(s);
		SerialPort port = (SerialPort) pID.open(s,0);
		port.setSerialPortParams(4800 ,SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	
		return port;
	}
	 

	public String scanForPort(ArrayList<String> ports) {
	
		//Goes through all available ComPorts and tries to listen for NMEA packages
		
		String output = "";
		String tmp;
		
		for(String port:ports) {
			try {
				SerialPort tmpPort = setupPort(port);
				tmp = readFromPort(tmpPort);
				
				//closing port after listening to be safe
				try {
					tmpPort.getInputStream().close();
					tmpPort.getOutputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				tmpPort.close();
				
				//Check for wanted expression
				if(tmp.contains("$GNTXT")||tmp.contains("$GPGSV")) {
					return port;
				}
				
			} catch (PortInUseException e) {
				e.printStackTrace();
			} catch (NoSuchPortException e) {
				e.printStackTrace();
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
			}
		}
		return output;
	} 
	
	public String readFromPort(SerialPort port) {
		
		//Reads Input via BufferedInputStream
		//while loop so there is no empty String being passed out
		
		InputStream is;
		try {
			is = port.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			while(bos.toString("UTF-8").isEmpty()) {
				for(int result = bis.read(); result != -1; result = bis.read()) {
					bos.write((byte) result);
				}
			}
			
			return bos.toString("UTF-8");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		String worstcase = "";
		return worstcase;
		
	}
	
	public String[] outputArray(SerialPort port) {
		
		//Fill an array with the NMEA Strings to pass on
		//Size is dependent on for loop
		
		String toArr="";
		String tmp;
		
		for(int i=0;i<5;i++) {
		tmp = readFromPort(port);
		tmp = tmp.replace("\r", "").replace("\n", "");
		toArr += tmp;
		}
		
		String[] output = toArr.split("\\$");
		return output;
	}
	
	
	public static void main(String[]args) {
		
		Hardware hw = new Hardware();
		
		//Figure out the correct Port
		String portName = hw.scanForPort(hw.getPorts());
		

			SerialPort port3;
			
			try {
				
				//setup port
				port3 = hw.setupPort(portName);
				
				//get output as Array
				String[] test = hw.outputArray(port3);
				
				//write it to console for test purposes
				for(String s:test) {
					System.out.println(s);
				}
				
				//close port again
				try {
					port3.getInputStream().close();
					port3.getOutputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				port3.close();
				
				//Solely to read the test output
				
				Scanner sc = new Scanner(System.in);
				sc.nextLine();
				sc.close();
				
			} catch (PortInUseException e) {
				e.printStackTrace();
			} catch (NoSuchPortException e) {
				e.printStackTrace();
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
			}
		
	}
}
