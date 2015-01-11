package com.example.intimateinterfaces;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.SharedPreferences;

/**
 * Send data to II server (needs to be on the same network as the application device)
 * 
 * @author IMDC
 *
 */
public class DataSender {

	InetAddress receiverAddress;
	DatagramSocket datagramSocket;
	
	public DataSender() {
		
	}
	
	private int getServerPort() {
		return 9090;
	}
	
	private String getServerIP() {
		return "10.232.153.174";
	}
	
	/**
	 * Using UDP packets, data will be sent to the II server
	 * whose IP and Port are defined in the Application settings
	 */
	public void sendData(DataConverter dataConverter) {
		
		String messegeStr= "APP::";// + dataConverter.getAnimationSpeed();
		int server_port = getServerPort();
		
		try {
			datagramSocket = new DatagramSocket();
			receiverAddress = InetAddress.getByName(getServerIP());
		 
			int msg_length = messegeStr.length();
			byte[] message = messegeStr.getBytes();
			DatagramPacket p = new DatagramPacket(message, msg_length,receiverAddress,server_port);
			datagramSocket.send(p);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
