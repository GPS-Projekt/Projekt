package Hardware;
import java.io.IOException;
import java.io.InputStream;
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
				if((byte) inputStream.read()=='$') break;
			};
			
			do {
				byteList.add((byte)inputStream.read());
			}while((byte) inputStream.read() != '$');
			
			Byte[] arr = byteList.toArray(new Byte[0]);
			byte[] bytes = new byte[arr.length];
			int i = 0;
			for(Byte b:arr)
				bytes[i++] = b.byteValue();
			
			String s = new String(bytes);
			output=s;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}
	
	public static void main(String[]args) {
		
		Test t = new Test();
		
		System.out.println(t.getPorts());
		
		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier("COM3");
			SerialPort port3 = (SerialPort) portIdentifier.open("COM3", 0);
			// might have to configure parameters still
			while(true) {
				System.out.println(t.readFromPort(port3));
			}
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			e.printStackTrace();
		}
		
	}
}
