package com.alibaba.easyexcel.test.temp.simple;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;

/**
 * 测试poi
 *
 * @author Jiaju Zhuang
 **/
@Ignore public class HgTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(HgTest.class);

  @Test public void hh() throws IOException {
    List<Object> list = EasyExcel.read(new FileInputStream("D:\\test\\\u5609\u60e0-\u4e2d\u4ea4\u5efa_2019-09-01_2019-09-30_1569055677522.xlsx", "D:\\test\\1.xls")).headRowNumber(0).sheet().doReadSync();
    for (Object data : list) {
      LOGGER.info("\u8fd4\u56de\u6570\u636e\uff1a{}", JSON.toJSONString(data));
    }
  }

  @Test public void hh5() throws IOException {
    URL url = new URL("http://hotelcontractfil.oss-cn-beijing.aliyuncs.com/2019/%E5%98%89%E6%83%A0-%E4%B8%AD%E4%BA%A4%E5%BB%BA_2019-09-01_2019-09-30_1569055677522.xlsx?Expires=1884415681&OSSAccessKeyId=LTAIGZDkqZfPArBr&Signature=Rf0gbO8vl3l%2Brj1KdyzHHMsUhCE%3D");
    InputStream is = url.openStream();
    List<Object> list = EasyExcel.read(is).headRowNumber(0).sheet().doReadSync();
    for (Object data : list) {
      LOGGER.info("\u8fd4\u56de\u6570\u636e\uff1a{}", JSON.toJSONString(data));
    }
  }

  @Test public void hh2() throws IOException {
    EasyExcel.read(new FileInputStream("D:\\test\\\u5546\u6237\u4e0d\u5339\u914d\u5de5\u5355\u4fe1\u606f\u6536\u96c6\u8868\u683c.xlsx")).registerReadListener(new HgListener()).headRowNumber(0).sheet().doRead();
  }
}