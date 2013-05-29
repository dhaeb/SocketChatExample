package de.eva.server.pojo;

public class Message {

	private String msg;
	private Client targetClient;

	public Message(String msg, Client targetClient) {
		this.msg = msg;
		this.targetClient = targetClient;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Client getTargetClient() {
		return targetClient;
	}

	public void setTargetClient(Client targetClient) {
		this.targetClient = targetClient;
	}
}
