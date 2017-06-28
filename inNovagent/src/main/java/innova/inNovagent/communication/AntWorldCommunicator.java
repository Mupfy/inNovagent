package innova.inNovagent.communication;

import jade.lang.acl.ACLMessage;

/**
 * Helper class to send abstracted commands to antworld. 
 *
 */
public interface AntWorldCommunicator {
	public void moveUp();
	public void moveDown();
	public void moveLeft();
	public void moveRight();

	public void pickUp();
	public void drop();
	
	public void login();
	/**
	 * Needs the last response from antworld for the next message.
	 * @param msg
	 */
	public void setLastMessage(ACLMessage msg);
}
