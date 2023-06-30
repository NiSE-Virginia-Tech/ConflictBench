package junit.runner;

/**
 * This class defines the current version of JUnit
 */
public class Version {
  private Version() {
  }

  public static String id() {
    return 
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/src/main/java/junit/runner/Version.java
    "4.11-SNAPSHOT"
=======
    "4.10"
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/src/main/java/junit/runner/Version.java
    ;
  }

  public static void main(String[] args) {
    System.out.println(id());
  }
}