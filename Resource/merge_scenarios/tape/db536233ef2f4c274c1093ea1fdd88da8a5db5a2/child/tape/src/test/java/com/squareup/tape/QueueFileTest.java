// Copyright 2010 Square, Inc.
package com.squareup.tape;

import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * Tests for QueueFile.
 *
 * @author Bob Lee (bob@squareup.com)
 */
@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class QueueFileTest {
  private static final Logger logger =
      Logger.getLogger(QueueFileTest.class.getName());

  /**
   * Takes up 33401 bytes in the queue (N*(N+1)/2+4*N). Picked 254 instead of
   * 255 so that the number of bytes isn't a multiple of 4.
   */
  private static int N = 254;
  private static byte[][] values = new byte[N][];

  static {
    for (int i = 0; i < N; i++) {
      byte[] value = new byte[i];
      // Example: values[3] = { 3, 2, 1 }
      for (int ii = 0; ii < i; ii++) value[ii] = (byte) (i - ii);
      values[i] = value;
    }
  }

  private File file;

  @Before public void setUp() throws Exception {
    file = File.createTempFile("test.queue", null);
    file.delete();
  }

  @After public void tearDown() throws Exception {
    file.delete();
  }

  @Test public void testAddOneElement() throws IOException {
    // This test ensures that we update 'first' correctly.
    QueueFile queue = new QueueFileJava(file);
    byte[] expected = values[253];
    queue.add(expected);
    assertThat(queue.peek()).isEqualTo(expected);
    queue.close();
    queue = new QueueFileJava(file);
    assertThat(queue.peek()).isEqualTo(expected);
  }

  @Test public void testAddAndRemoveElements() throws IOException {
    long start = System.nanoTime();

    Queue<byte[]> expected = new LinkedList<byte[]>();

    for (int round = 0; round < 5; round++) {
      QueueFile queue = new QueueFileJava(file);
      for (int i = 0; i < N; i++) {
        queue.add(values[i]);
        expected.add(values[i]);
      }

      // Leave N elements in round N, 15 total for 5 rounds. Removing all the
      // elements would be like starting with an empty queue.
      for (int i = 0; i < N - round - 1; i++) {
        assertThat(queue.peek()).isEqualTo(expected.remove());
        queue.remove();
      }
      queue.close();
    }

    // Remove and validate remaining 15 elements.
    QueueFile queue = new QueueFileJava(file);
    assertThat(queue.size()).isEqualTo(15);
    assertThat(queue.size()).isEqualTo(expected.size());
    while (!expected.isEmpty()) {
      assertThat(queue.peek()).isEqualTo(expected.remove());
      queue.remove();
    }
    queue.close();

    // length() returns 0, but I checked the size w/ 'ls', and it is correct.
    // assertEquals(65536, file.length());

    logger.info("Ran in " + ((System.nanoTime() - start) / 1000000) + "ms.");
  }

  /** Tests queue expansion when the data crosses EOF. */
  @Test public void testSplitExpansion() throws IOException {
    // This should result in 3560 bytes.
    int max = 80;

    Queue<byte[]> expected = new LinkedList<byte[]>();
    QueueFile queue = new QueueFileJava(file);

    for (int i = 0; i < max; i++) {
      expected.add(values[i]);
      queue.add(values[i]);
    }

    // Remove all but 1.
    for (int i = 1; i < max; i++) {
      assertThat(queue.peek()).isEqualTo(expected.remove());
      queue.remove();
    }

    // This should wrap around before expanding.
    for (int i = 0; i < N; i++) {
      expected.add(values[i]);
      queue.add(values[i]);
    }

    while (!expected.isEmpty()) {
      assertThat(queue.peek()).isEqualTo(expected.remove());
      queue.remove();
    }

    queue.close();
  }

  @Test public void testFailedAdd() throws IOException {
    QueueFile queueFile = new QueueFileJava(file);
    queueFile.add(values[253]);
    queueFile.close();

    final BrokenRandomAccessFile braf = new BrokenRandomAccessFile(file, "rwd");
    queueFile = new QueueFileJava(braf);

    try {
      queueFile.add(values[252]);
      fail();
    } catch (IOException e) { /* expected */ }

    braf.rejectCommit = false;

    // Allow a subsequent add to succeed.
    queueFile.add(values[251]);

    queueFile.close();

    queueFile = new QueueFileJava(file);
    Assertions.assertThat(queueFile.size()).isEqualTo(2);
    assertThat(queueFile.peek()).isEqualTo(values[253]);
    queueFile.remove();
    assertThat(queueFile.peek()).isEqualTo(values[251]);
  }

  @Test public void testFailedRemoval() throws IOException {
    QueueFile queueFile = new QueueFileJava(file);
    queueFile.add(values[253]);
    queueFile.close();

    final BrokenRandomAccessFile braf = new BrokenRandomAccessFile(file, "rwd");
    queueFile = new QueueFileJava(braf);

    try {
      queueFile.remove();
      fail();
    } catch (IOException e) { /* expected */ }

    queueFile.close();

    queueFile = new QueueFileJava(file);
    assertThat(queueFile.size()).isEqualTo(1);
    assertThat(queueFile.peek()).isEqualTo(values[253]);

    queueFile.add(values[99]);
    queueFile.remove();
    assertThat(queueFile.peek()).isEqualTo(values[99]);
  }

  @Test public void testFailedExpansion() throws IOException {
    QueueFileJava queueFile = new QueueFileJava(file);
    queueFile.add(values[253]);
    queueFile.close();

    final BrokenRandomAccessFile braf = new BrokenRandomAccessFile(file, "rwd");
    queueFile = new QueueFileJava(braf);

    try {
      // This should trigger an expansion which should fail.
      queueFile.add(new byte[8000]);
      fail();
    } catch (IOException e) { /* expected */ }

    queueFile.close();

    queueFile = new QueueFileJava(file);

    assertThat(queueFile.size()).isEqualTo(1);
    assertThat(queueFile.peek()).isEqualTo(values[253]);
    assertThat(queueFile.getFileLength()).isEqualTo(4096);

    queueFile.add(values[99]);
    queueFile.remove();
    assertThat(queueFile.peek()).isEqualTo(values[99]);
  }

  @Test public void testPeekWithElementReader() throws IOException {
    QueueFile queueFile = new QueueFileJava(file);
    final byte[] a = {1, 2};
    queueFile.add(a);
    final byte[] b = {3, 4, 5};
    queueFile.add(b);

    final AtomicInteger peeks = new AtomicInteger(0);

    queueFile.peek(new QueueFile.ElementReader() {
      @Override public void read(InputStream in, int length) throws IOException {
        peeks.incrementAndGet();

        assertThat(length).isEqualTo(2);
        byte[] actual = new byte[length];
        in.read(actual);
        assertThat(actual).isEqualTo(a);
      }
    });

    queueFile.peek(new QueueFileJava.ElementReader() {
      @Override public void read(InputStream in, int length) throws IOException {
        peeks.incrementAndGet();

        assertThat(length).isEqualTo(2);
        assertThat(in.read()).isEqualTo(1);
        assertThat(in.read()).isEqualTo(2);
        assertThat(in.read()).isEqualTo(-1);
      }
    });

    queueFile.remove();

    queueFile.peek(new QueueFileJava.ElementReader() {
      @Override public void read(InputStream in, int length) throws IOException {
        peeks.incrementAndGet();

        assertThat(length).isEqualTo(3);
        byte[] actual = new byte[length];
        in.read(actual);
        assertThat(actual).isEqualTo(b);
      }
    });

    assertThat(peeks.get()).isEqualTo(3);
    assertThat(queueFile.peek()).isEqualTo(b);
    assertThat(queueFile.size()).isEqualTo(1);
  }

  @Test public void testForEach() throws IOException {
    QueueFile queueFile = new QueueFileJava(file);

    final byte[] a = {1, 2};
    queueFile.add(a);
    final byte[] b = {3, 4, 5};
    queueFile.add(b);

    final int[] iteration = new int[]{0};
    QueueFile.ElementReader elementReader = new QueueFileJava.ElementReader() {
      @Override public void read(InputStream in, int length) throws IOException {
        if (iteration[0] == 0) {
          assertThat(length).isEqualTo(2);
          byte[] actual = new byte[length];
          in.read(actual);
          assertThat(actual).isEqualTo(a);
        } else if (iteration[0] == 1) {
          assertThat(length).isEqualTo(3);
          byte[] actual = new byte[length];
          in.read(actual);
          assertThat(actual).isEqualTo(b);
        } else {
          fail();
        }
        iteration[0]++;
      }
    };

    queueFile.forEach(elementReader);

    assertThat(queueFile.peek()).isEqualTo(a);
    assertThat(iteration[0]).isEqualTo(2);
  }

  /**
   * Exercise a bug where wrapped elements were getting corrupted when the
   * QueueFile was forced to expand in size and a portion of the final Element
   * had been wrapped into space at the beginning of the file.
   */
  @Test public void testFileExpansionDoesntCorruptWrappedElements()
      throws IOException {
    QueueFile queue = new QueueFileJava(file);

    // Create test data - 1k blocks marked consecutively 1, 2, 3, 4 and 5.
    byte[][] values = new byte[5][];
    for (int blockNum = 0; blockNum < values.length; blockNum++) {
      values[blockNum] = new byte[1024];
      for (int i = 0; i < values[blockNum].length; i++) {
        values[blockNum][i] = (byte) (blockNum + 1);
      }
    }

    // First, add the first two blocks to the queue, remove one leaving a
    // 1K space at the beginning of the buffer.
    queue.add(values[0]);
    queue.add(values[1]);
    queue.remove();

    // The trailing end of block "4" will be wrapped to the start of the buffer.
    queue.add(values[2]);
    queue.add(values[3]);

    // Cause buffer to expand as there isn't space between the end of block "4"
    // and the start of block "2".  Internally the queue should cause block "4"
    // to be contiguous, but there was a bug where that wasn't happening.
    queue.add(values[4]);

    // Make sure values are not corrupted, specifically block "4" that wasn't
    // being made contiguous in the version with the bug.
    for (int blockNum = 1; blockNum < values.length; blockNum++) {
      byte[] value = queue.peek();
      queue.remove();

      for (int i = 0; i < value.length; i++) {
        assertThat(value[i]).isEqualTo((byte) (blockNum + 1))
            .as("Block " + (blockNum + 1) + " corrupted at byte index " + i);
      }
    }

    queue.close();
  }

  /**
   * Exercise a bug where wrapped elements were getting corrupted when the
   * QueueFile was forced to expand in size and a portion of the final Element
   * had been wrapped into space at the beginning of the file - if multiple
   * Elements have been written to empty buffer space at the start does the
   * expansion correctly update all their positions?
   */
  @Test public void testFileExpansionCorrectlyMovesElements() throws IOException {
    QueueFile queue = new QueueFileJava(file);

    // Create test data - 1k blocks marked consecutively 1, 2, 3, 4 and 5.
    byte[][] values = new byte[5][];
    for (int blockNum = 0; blockNum < values.length; blockNum++) {
      values[blockNum] = new byte[1024];
      for (int i = 0; i < values[blockNum].length; i++) {
        values[blockNum][i] = (byte) (blockNum + 1);
      }
    }

    // smaller data elements
    byte[][] smaller = new byte[3][];
    for (int blockNum = 0; blockNum < smaller.length; blockNum++) {
      smaller[blockNum] = new byte[256];
      for (int i = 0; i < smaller[blockNum].length; i++) {
        smaller[blockNum][i] = (byte) (blockNum + 6);
      }
    }

    // First, add the first two blocks to the queue, remove one leaving a
    // 1K space at the beginning of the buffer.
    queue.add(values[0]);
    queue.add(values[1]);
    queue.remove();

    // The trailing end of block "4" will be wrapped to the start of the buffer.
    queue.add(values[2]);
    queue.add(values[3]);

    // Now fill in some space with smaller blocks, none of which will cause
    // an expansion.
    queue.add(smaller[0]);
    queue.add(smaller[1]);
    queue.add(smaller[2]);

    // Cause buffer to expand as there isn't space between the end of the
    // smaller block "8" and the start of block "2".  Internally the queue
    // should cause all of tbe smaller blocks, and the trailing end of
    // block "5" to be moved to the end of the file.
    queue.add(values[4]);

    byte[] expectedBlockNumbers = {2, 3, 4, 6, 7, 8, 5};

    // Make sure values are not corrupted, specifically block "4" that wasn't
    // being made contiguous in the version with the bug.
    for (byte expectedBlockNumber : expectedBlockNumbers) {
      byte[] value = queue.peek();
      queue.remove();

      for (int i = 0; i < value.length; i++) {
        assertThat(value[i]).isEqualTo(expectedBlockNumber)
            .as("Block " + expectedBlockNumber + " corrupted at byte index " + i);
      }
    }

    queue.close();
  }

  /**
   * A RandomAccessFile that can break when you go to write the COMMITTED
   * status.
   */
  static class BrokenRandomAccessFile extends RandomAccessFile {
    boolean rejectCommit = true;

    BrokenRandomAccessFile(File file, String mode)
        throws FileNotFoundException {
      super(file, mode);
    }

    @Override public void write(byte[] buffer) throws IOException {
      if (rejectCommit && getFilePointer() == 0) {
        throw new IOException("No commit for you!");
      }
      super.write(buffer);
    }
  }
}
