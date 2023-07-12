package spock.lang;
import groovy.lang.Closure;
import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.METHOD }) public @interface Unroll {
  Class<? extends Closure> value() default 
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/spock-core/src/main/java/spock/lang/Unroll.java
  Closure.class
=======
  "#featureName[#iterationCount]"
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/spock-core/src/main/java/spock/lang/Unroll.java
  ;
}