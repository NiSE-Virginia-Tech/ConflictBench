package junit.runner;

/**
 * This class defines the current version of JUnit
 */
public class Version {
 private Version() {
		// don't instantiate
	}
 
 public static String id() {
<<<<<<< ours
		return "4.11-SNAPSHOT";
=======
		return "4.10";
>>>>>>> theirs
	}
 
 public static void main(String[] args) {
		System.out.println(id());
	}
}