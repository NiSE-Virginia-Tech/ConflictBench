package org.datatransferproject.transfer.smugmug.photos;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SmugMugInterfaceTest {
  @Test public void cleanName_standard() {
    assertEquals(SmugMugInterface.cleanName("MyAlbum"), "MyAlbum");
  }

  @Test public void cleanName_punctuation() {
    assertEquals(SmugMugInterface.cleanName("MyAlbum!"), "MyAlbum");
  }

  @Test public void cleanName_spaces() {
    assertEquals(SmugMugInterface.cleanName("My Album"), "My-Album");
  }

  @Test public void cleanName_long() {
    assertEquals(SmugMugInterface.cleanName("My Album From That One Time I did an Activity and took several" + " pictures of it"), "My-Album-From-That-One-Time-I-did-an-Act");
  }

  @Test public void cleanName_NonLatin() {
    assertEquals(SmugMugInterface.cleanName("\u10e9\u10d4\u10db\u10d8 \u10e4\u10dd\u10e2\u10dd\u10d4\u10d1\u10d8"), "\u10e9\u10d4\u10db\u10d8-\u10e4\u10dd\u10e2\u10dd\u10d4\u10d1\u10d8");
  }

  @Test public void cleanName_AllWrong() {
    assertEquals(SmugMugInterface.cleanName("\ud83d\udd25"), "");
  }
}