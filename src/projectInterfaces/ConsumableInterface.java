package projectInterfaces;

//Public interface for consumable game objects s.a. food or poison
public interface ConsumableInterface {
	
	//Method to activate the effects of the consumable object
	public void Consume(SnakeInterface consumedBy);
	
}
