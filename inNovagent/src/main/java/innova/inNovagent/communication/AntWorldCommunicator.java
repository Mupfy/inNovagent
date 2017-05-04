package innova.inNovagent.communication;

import jade.lang.acl.ACLMessage;

public interface AntWorldCommunicator {
	public void moveUp();
	public void moveDown();
	public void moveLeft();
	public void moveRight();

	public void pickUp();
	public void drop();
	
	public void login();
	public void setLastMessage(ACLMessage msg);
}
