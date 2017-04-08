package innova.inNovagent.agents;

public interface AgentObserver {
	public void agentModified(SynchronizedAgent agent, Object type, Object... args);
}
