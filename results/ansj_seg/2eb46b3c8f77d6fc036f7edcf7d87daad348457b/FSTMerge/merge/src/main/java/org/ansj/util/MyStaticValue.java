package org.ansj.util; 

import java.io.BufferedReader; 
import java.io.File; 
import java.io.IOException; 
import java.io.InputStream; 
import java.io.ObjectInputStream; 
import java.io.UnsupportedEncodingException; 
import java.util.HashMap; 
import java.util.Map; 
import java.util.PropertyResourceBundle; 
import java.util.ResourceBundle; 
import java.util.concurrent.locks.Lock; 
import java.util.concurrent.locks.ReentrantLock; 
import java.util.logging.Logger; 

import org.ansj.app.crf.Model; 
import org.ansj.app.crf.SplitWord; 
import org.ansj.dic.DicReader; 
import org.ansj.domain.AnsjItem; 
import org.ansj.library.DATDictionary; 
import org.nlpcn.commons.lang.util.FileFinder; 
import org.nlpcn.commons.lang.util.IOUtil; 
import org.nlpcn.commons.lang.util.StringUtil; 

/**
 * 这个类储存一些公用变量.
 * 
 * @author ansj
 * 
 */
  class  MyStaticValue {
	

    

	

    // 是否开启人名识别
    

	

    

	

    // 是否开启数字识别
    

	

    // 是否数字和量词合并
    

	

    // crf 模型

    

	

    

	

    /**
     * 用户自定义词典的加载,如果是路径就扫描路径下的dic文件
     */
    

	

    

	
    
    public static String crfModel = "library/crf.model";

	

    /**
     * 是否用户辞典不加载相同的词
     */
    

	

    static {
        /**
         * 配置文件变量
         */
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
        } else {

            if (rb.containsKey("userLibrary"))
                userLibrary = rb.getString("userLibrary");
            if (rb.containsKey("ambiguityLibrary"))
                ambiguityLibrary = rb.getString("ambiguityLibrary");
            if (rb.containsKey("isSkipUserDefine"))
                isSkipUserDefine = Boolean.valueOf(rb.getString("isSkipUserDefine"));
            if (rb.containsKey("isRealName"))
                isRealName = Boolean.valueOf(rb.getString("isRealName"));
            if (rb.containsKey("crfModel"))
                crfModel = rb.getString("crfModel");
        }
    }

	

    /**
     * 人名词典
     * 
     * @return
     */
    

	

    /**
     * 机构名词典
     * 
     * @return
     */
    

	

    /**
     * 机构名词典
     * 
     * @return
     */
    

	

    /**
     * 核心词典
     * 
     * @return
     */
    

	

    /**
     * 数字词典
     * 
     * @return
     */
    

	

    /**
     * 英文词典
     * 
     * @return
     */
    

	

    /**
     * 词性表
     * 
     * @return
     */
    

	

    /**
     * 词性关联表
     * 
     * @return
     */
    

	

    /**
     * 得道姓名单字的词频词典
     * 
     * @return
     */
    

	

    /**
     * 名字词性对象反序列化
     * 
     * @return
     */
    

	

    /**
     * 词与词之间的关联表数据
     * 
     * @return
     */
    

	

    /**
     * 得到默认的模型
     * 
     * @return
     */
    <<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647878456086/fstmerge_var1_2696956695290423132
=======
public static SplitWord getCRFSplitWord() {
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
            crfSplitWord = new SplitWord(Model.loadModel(IOUtil.getInputStream(crfModel)));
            LIBRARYLOG.info("load crf crf use time:" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }

        return crfSplitWord;
    }
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647878456086/fstmerge_var2_3041617910861470425


	

    static {
        /**
         * 配置文件变量
         */
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

        if (rb.containsKey("userLibrary"))
            userLibrary = rb.getString("userLibrary");
        if (rb.containsKey("ambiguityLibrary"))
            ambiguityLibrary = rb.getString("ambiguityLibrary");
        if (rb.containsKey("isSkipUserDefine"))
            isSkipUserDefine = Boolean.valueOf(rb.getString("isSkipUserDefine"));
        if (rb.containsKey("isRealName"))
            isRealName = Boolean.valueOf(rb.getString("isRealName"));
    }


}
