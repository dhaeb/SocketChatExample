package de.eva.server.pojo;

import java.net.InetAddress;

public class Client {

	private String name;
	private InetAddress host;
	private int port;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public InetAddress getHost() {
		return host;
	}
	
	public void setHost(InetAddress host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	} 

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if(obj != null && obj instanceof Client){
			Client comparable = (Client) obj;
			isEqual = this.name.equals(comparable.getName()) 
				   && this.host.equals(comparable.getHost())
			       && this.port == comparable.getPort();
		}
		return isEqual; 
	}
	
	public static Client createClient(String name, InetAddress host, int port){
		Client client = new Client();
		client.setName(name);
		client.setHost(host);
		client.setPort(port);
		return client;
	}
}
