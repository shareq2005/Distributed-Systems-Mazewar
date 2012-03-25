import java.util.*;

public class VectorClockList {

	/**
	 * 
	 * List of the 4 vector clocks
	 */
	public static VectorClock clock_zero = new VectorClock();
	public static VectorClock clock_one = new VectorClock();
	public static VectorClock clock_two = new VectorClock();
	public static VectorClock clock_three = new VectorClock();

	/**
	 * Method to initialize all vector clocks
	 */
	public static void intialize_vector_clocks(int client_id)
	{

		if(client_id == 0) {
			//Add clients 0 to 3 (client IDs) for each vector clock
			clock_zero.clear();
			clock_zero.put("0", 0);
			clock_zero.put("1", 0);
			clock_zero.put("2", 0);
			clock_zero.put("3", 0);
		}
		else if(client_id == 1) {
			//Add clients 0 to 3 (client IDs) for each vector clock
			clock_one.clear();
			clock_one.put("0", 0);
			clock_one.put("1", 0);
			clock_one.put("2", 0);
			clock_one.put("3", 0);
		}
		else if(client_id == 2) {
			//Add clients 0 to 3 (client IDs) for each vector clock
			clock_two.clear();
			clock_two.put("0", 0);
			clock_two.put("1", 0);
			clock_two.put("2", 0);
			clock_two.put("3", 0);
		}
		else if(client_id == 3) {
			//Add clients 0 to 3 (client IDs) for each vector clock
			clock_three.clear();
			clock_three.put("0", 0);
			clock_three.put("1", 0);
			clock_three.put("2", 0);
			clock_three.put("3", 0);
		};
	}
	/**
	 * @param client_id - this is the client ID of which clock 
	 */
	public static void increment_vector_clock(int client_id) 
	{
		if(client_id == 0)
		{
			clock_zero.incrementClock("0");
		}
		else if(client_id == 1)
		{
			clock_one.incrementClock("1");
		}
		else if(client_id == 2)
		{
			clock_two.incrementClock("2");
		}
		else if(client_id == 3)
		{
			clock_three.incrementClock("3");
		};
	}
}