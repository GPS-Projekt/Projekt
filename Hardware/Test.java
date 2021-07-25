package Hardware;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;

import gnu.io.*;

public class Test {
	
	//Still missing a way to choose the commport both manually and automatically
	
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
		
		CommPortIdentifier pID = CommPortIdentifier.getPortIdentifier(s);
		SerialPort port = (SerialPort) pID.open(s,0);
		port.setSerialPortParams(4800,SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	
		return port;
	}
	
	/*
	public String readFromPort(SerialPort port) {
		
		//reads from the given serialport 
		//idea here was to read from first $ to the next $ but still having issues with $ disappearing etc.
		//ByteList->Byte[]->byte[]->String as a workaround to get readable text.
		//There might be an easier way but works for now
		
		InputStream inputStream;
		String output="";
		ArrayList<Byte> byteList = new ArrayList<>();
		
		try {
			
			inputStream = port.getInputStream();
			
			while((byte)inputStream.read() !='$') {
				if((byte) inputStream.read()=='$') {
					do {
						byteList.add((byte)inputStream.read());
					}while((byte) inputStream.read() != '$');
				};
			};
			

			
			Byte[] arr = byteList.toArray(new Byte[0]);
			byte[] bytes = new byte[arr.length];
			int i = 0;
			for(Byte b:arr)
				bytes[i++] = b.byteValue();
			
			String s = new String(bytes, "UTF-8");
			output=s;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}
	*/
	
	public String readFromPort(SerialPort port) {
		
		//reworked read method with external input due to lacking knowledge regarding streams
		//first tests with buffered reader had issues converting the byte output to string
		//ByteArrayOutputStream fixed that issue
		
		InputStream is;
		try {
			is = port.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			for(int result = bis.read(); result != -1; result = bis.read()) {
				bos.write((byte) result);
			}
		
			return bos.toString("UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String worstcase = "";
		return worstcase;
		
	}
	
	public static void main(String[]args) {
		
		Test t = new Test();
		
		System.out.println(t.getPorts());
		

			SerialPort port3;
			try {
				port3 = t.setupPort("COM3");

				while(true) {
					System.out.println(t.readFromPort(port3));
				}
			} catch (PortInUseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedCommOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}
