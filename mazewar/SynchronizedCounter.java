import java.io.*;
import java.util.*;

/**
 * 
 * @author Syed Shareq Rabbani
 * 
 * @brief synchronized counter
 *
 */
public class SynchronizedCounter {

	private static int sequence_number = 0;

    public synchronized static int grant_sequence() {
        sequence_number++;
        return sequence_number;
    }

}