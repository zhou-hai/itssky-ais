package com.itssky.xuzhsAIS.netty.client;


import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.nmea.NMEAMessageHandler;
import dk.tbsalling.aismessages.nmea.NMEAMessageSocketClient;
 
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Consumer;


public class SocketDemoApp implements Consumer<AISMessage>{

	@Override
    public void accept(AISMessage aisMessage) {
        System.out.println("Received AIS message: " + aisMessage);
    }
 
	public void runDemo() {
		System.out.println("AISMessages Demo App");
		System.out.println("--------------------");
 
		try {
			// NMEAMessageSocketClient nmeaMessageHandler = new NMEAMessageSocketClient("207.7.148.216", 9009, new NMEAMessageHandler("DEMOSRC1", this));
			NMEAMessageSocketClient nmeaMessageHandler = new NMEAMessageSocketClient("ais.exploratorium.edu", 80, new NMEAMessageHandler("DEMOSRC1", this));
			nmeaMessageHandler.run();
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("I/O error: " + e.getMessage());
		}
	}
 
	public static void main(String[] args) {
		new SocketDemoApp().runDemo();
	}
	
}
