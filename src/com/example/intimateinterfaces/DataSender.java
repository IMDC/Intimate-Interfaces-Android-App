package com.example.intimateinterfaces;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Send data to II server (needs to be on the same network as the application device)
 * 
 * @author IMDC
 */
public class DataSender {

	private InetAddress receiverAddress;
	private DatagramSocket datagramSocket;
	private Context context;
	
	public DataSender(Context context) {
		//need the context in order to access the shared preferences
		this.context = context;
	}
	
	private int getServerPort() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
		int serverPort = Integer.parseInt(prefs.getString("pref_server_port", "9090"));
		return serverPort;
	}
	
	private String getServerIP() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
		String serverIP = prefs.getString("pref_server", "127.0.0.1");
		return serverIP;
	}
	
	/**
	 * Using UDP packets, data will be sent to the II server
	 * whose IP and Port are defined in the Application settings
	 */
	public void sendData(DataConverter dataConverter) {
		
		String messegeStr= "" + dataConverter.getAnimationSpeed();
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
