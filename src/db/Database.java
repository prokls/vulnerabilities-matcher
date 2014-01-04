/**
 * The database connection implementation.
 * Retrieves ServerSoftwareTuple instances from the DB dataset. 
 */
package db;

import java.util.HashSet;
import java.util.Set;

public class Database {
	public Database() {
		
	}
	
	public void connect() {
		// TODO: retrieve credentials from configuration file
		//       (define config file yourself)
		// TODO: connect to database and store connection object in object member 
	}
	
	public Set<ServerSoftwareTuple> getTuples() {
		// TODO: create new instances of ServerSoftwareTuple
		//       for each entry of software package per hostname
		return new HashSet<ServerSoftwareTuple>();
	}
	
	public void disconnect() {
		// TODO: disconnect from database
	}
}
