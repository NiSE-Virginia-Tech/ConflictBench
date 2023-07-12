package org.ansj.util;
import lombok.SneakyThrows;
import org.ansj.app.crf.Model;
import java.io.BufferedReader;
import org.ansj.app.crf.SplitWord;
import java.io.File;
import org.ansj.domain.AnsjItem;
import java.io.ObjectInputStream;
import org.ansj.library.DATDictionary;
import java.util.HashMap;
import org.nlpcn.commons.lang.util.FileFinder;
import java.util.Map;
import org.nlpcn.commons.lang.util.IOUtil;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 这个类储存一些公用变量.
 *
 * @author ansj
 */
public class MyStaticValue {
  public static final Logger LIBRARYLOG = Logger.getLogger("DICLOG");

  public static boolean isNameRecognition = true;

  private static final Lock LOCK = new ReentrantLock();

  public static boolean isNumRecognition = true;

  public static boolean isQuantifierRecognition = true;

  private static SplitWord crfSplitWord = null;

  public static boolean isRealName = false;

  /**
     * 用户自定义词典的加载,如果是路径就扫描路径下的dic文件
     */
  public static String userLibrary = "library/default.dic";

  public static String ambiguityLibrary = "library/ambiguity.dic";

  public static String crfModel = "library/crf.model";

  /**
     * 是否用户辞典不加载相同的词
     */
  public static boolean isSkipUserDefine = false;

  static {

    init();
  }

  @SneakyThrows private static void init() {
    ResourceBundle rb = null;
    try {
      rb = ResourceBundle.getBundle("library");
    } catch (Exception e) {
      try {
        File find = FileFinder.find("library.properties");
        if (find != null) {
          rb = new PropertyResourceBundle(IOUtil.getReader(find.getAbsolutePath(), System.getProperty("file.encoding")));
          LIBRARYLOG.info("load library not find in classPath ! i find it in " + find.getAbsolutePath() + " make sure it is your config!");
        }
      } catch (Exception e1) {
        LIBRARYLOG.warning("not find library.properties. and err " + e.getMessage() + " i think it is a bug!");
      }
    }
    if (rb == null) {
      LIBRARYLOG.warning("not find library.properties in classpath use it by default !");
    }
    if (rb.containsKey("userLibrary")) {
      userLibrary = rb.getString("userLibrary");
    }
    if (rb.containsKey("ambiguityLibrary")) {
      ambiguityLibrary = rb.getString("ambiguityLibrary");
    }
    if (rb.containsKey("isSkipUserDefine")) {
      isSkipUserDefine = Boolean.valueOf(rb.getString("isSkipUserDefine"));
    }
    if (rb.containsKey("isRealName")) {
      isRealName = Boolean.valueOf(rb.getString("isRealName"));
    }
  }

  /**
     * 人名词典
     */
  public static BufferedReader getPersonReader() {
    return AnsjUtils.getReader("person/person.dic");
  }

  /**
     * 机构名词典
     */
  public static BufferedReader getCompanReader() {
    return AnsjUtils.getReader("company/company.data");
  }

  /**
     * 词性表
     */
  public static BufferedReader getNatureMapReader() {
    return AnsjUtils.getReader("nature/nature.map");
  }

  /**
     * 机构名词典
     * 
     * @return
     */
  public static BufferedReader getNewWordReader() {
    return AnsjUtils.getReader("newWord/new_word_freq.dic");
  }

  /**
     * 词性关联表
     */
  public static BufferedReader getNatureTableReader() {
    return AnsjUtils.getReader("nature/nature.table");
  }

  /**
     * 核心词典
     * 
     * @return
     */
  public static BufferedReader getArraysReader() {
    return AnsjUtils.getReader("arrays.dic");
  }

  /**
     * 得道姓名单字的词频词典
     */
  public static BufferedReader getPersonFreqReader() {
    return AnsjUtils.getReader("person/name_freq.dic");
  }

  /**
     * 数字词典
     * 
     * @return
     */
  public static BufferedReader getNumberReader() {
    return AnsjUtils.getReader("numberLibrary.dic");
  }

  /**
     * 英文词典
     * 
     * @return
     */
  public static BufferedReader getEnglishReader() {
    return AnsjUtils.getReader("englishLibrary.dic");
  }

  /**
     * 名字词性对象反序列化
     */
  @SneakyThrows @SuppressWarnings(value = { "unchecked" }) public static Map<String, int[][]> getPersonFreqMap() {
    try (ObjectInputStream os = new ObjectInputStream(AnsjUtils.getInputStream("person/asian_name_freq.data"))) {
      return (Map<String, int[][]>) os.readObject();
    }
  }

  /**
     * 词与词之间的关联表数据
     */
  @SneakyThrows public static void initBigramTables() {
    try (BufferedReader reader = IOUtil.getReader(AnsjUtils.getInputStream("bigramdict.dic"), "UTF-8")) {
      String temp;
      while ((temp = reader.readLine()) != null) {
        if (isBlank(temp)) {
          continue;
        }
        final String[] split = temp.split("\t");
        final int freq = Integer.parseInt(split[1]);
        final String[] strs = split[0].split("@");
        AnsjItem fromItem = DATDictionary.getItem(strs[0]);
        AnsjItem toItem = DATDictionary.getItem(strs[1]);
        if (fromItem == AnsjItem.NULL && strs[0].contains("#")) {
          fromItem = AnsjItem.BEGIN;
        }
        if (toItem == AnsjItem.NULL && strs[1].contains("#")) {
          toItem = AnsjItem.END;
        }
        if (fromItem == AnsjItem.NULL || toItem == AnsjItem.NULL) {
          continue;
        }
        if (fromItem.bigramEntryMap == null) {
          fromItem.bigramEntryMap = new HashMap<>();
        }
        fromItem.bigramEntryMap.put(toItem.getIndex(), freq);
      }
    }
  }

  /**
     * 得到默认的模型
     */
  @SneakyThrows public static SplitWord getCRFSplitWord() {
    if (crfSplitWord != null) {
      return crfSplitWord;
    }
    LOCK.lock();
    if (crfSplitWord != null) {
      return crfSplitWord;
    }
    try {
      long start = System.currentTimeMillis();
      LIBRARYLOG.info("begin init crf model!");
      crfSplitWord = new SplitWord(Model.loadModel(
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/src/main/java/org/ansj/util/MyStaticValue.java
      AnsjUtils
=======
      IOUtil
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/src/main/java/org/ansj/util/MyStaticValue.java
      .getInputStream(crfModel)));
      LIBRARYLOG.info("load crf crf use time:" + (System.currentTimeMillis() - start));
    }  finally {
      LOCK.unlock();
    }
    return crfSplitWord;
  }
}