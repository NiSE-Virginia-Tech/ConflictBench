package org.wltea.analyzer.sample;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 使用IKAnalyzer进行Lucene索引和查询的演示
 * 2012-3-2
 * 
 * 以下是结合Lucene4.0 API的写法
 *
 */
public class LuceneIndexAndSearchDemo {
  public static final ESLogger logger = Loggers.getLogger("ik-analyzer");

  /**
	 * 模拟：
	 * 创建一个单条记录的索引，并对其进行搜索
	 * @param args
	 */
  public static void main(String[] args) {
    String fieldName = "text";
    String text = "IK Analyzer\u662f\u4e00\u4e2a\u7ed3\u5408\u8bcd\u5178\u5206\u8bcd\u548c\u6587\u6cd5\u5206\u8bcd\u7684\u4e2d\u6587\u5206\u8bcd\u5f00\u6e90\u5de5\u5177\u5305\u3002\u5b83\u4f7f\u7528\u4e86\u5168\u65b0\u7684\u6b63\u5411\u8fed\u4ee3\u6700\u7ec6\u7c92\u5ea6\u5207\u5206\u7b97\u6cd5\u3002";
    Analyzer analyzer = new IKAnalyzer(true);
    Directory directory = null;
    IndexWriter iwriter = null;
    IndexReader ireader = null;
    IndexSearcher isearcher = null;
    try {
      directory = new RAMDirectory();
      IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
      iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
      iwriter = new IndexWriter(directory, iwConfig);
      Document doc = new Document();
      doc.add(new StringField("ID", "10000", Field.Store.YES));
      doc.add(new TextField(fieldName, text, Field.Store.YES));
      iwriter.addDocument(doc);
      iwriter.close();
      ireader = DirectoryReader.open(directory);
      isearcher = new IndexSearcher(ireader);
      String keyword = "\u4e2d\u6587\u5206\u8bcd\u5de5\u5177\u5305";
      QueryParser qp = new QueryParser(fieldName, analyzer);
      qp.setDefaultOperator(QueryParser.AND_OPERATOR);
      Query query = qp.parse(keyword);
      System.out.println("Query = " + query);
      TopDocs topDocs = isearcher.search(query, 5);
      System.out.println("\u547d\u4e2d\uff1a" + topDocs.totalHits);
      ScoreDoc[] scoreDocs = topDocs.scoreDocs;
      for (int i = 0; i < topDocs.totalHits; i++) {
        Document targetDoc = isearcher.doc(scoreDocs[i].doc);
        System.out.println("\u5185\u5bb9\uff1a" + targetDoc.toString());
      }
    } catch (CorruptIndexException e) {
      logger.error(e.getMessage(), e);
    } catch (LockObtainFailedException e) {
      logger.error(e.getMessage(), e);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } catch (ParseException e) {
      logger.error(e.getMessage(), e);
    } finally {
      if (ireader != null) {
        try {
          ireader.close();
        } catch (IOException e) {
          logger.error(e.getMessage(), e);
        }
      }
      if (directory != null) {
        try {
          directory.close();
        } catch (IOException e) {
          logger.error(e.getMessage(), e);
        }
      }
    }
  }
}