package org.dna.mqtt.moquette.server;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.dna.mqtt.moquette.client.Client;
import org.dna.mqtt.moquette.client.IPublishCallback;
import org.dna.mqtt.moquette.proto.messages.AbstractMessage;
import org.fusesource.mqtt.client.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author andrea
 */
public class ServerIntegrationTest {
  boolean received;

  Server server;

  protected void startServer() throws IOException {
    server = new Server();
    server.startServer();
  }

  @Before public void setUp() throws IOException {
    startServer();
  }

  @After public void tearDown() {
    server.stopServer();
    File dbFile = new File(Server.STORAGE_FILE_PATH);
    if (dbFile.exists()) {
      dbFile.delete();
    }
  }

  @Test public void testSubscribe_FSClient() throws Exception {
    MQTT mqtt = new MQTT();
    mqtt.setHost("localhost", Server.PORT);
    mqtt.setClientId("TestClient");
    FutureConnection connection = mqtt.futureConnection();
    connection.connect().await();
    Topic[] topics = { new Topic("/topic", QoS.AT_MOST_ONCE) };
    Future<byte[]> futSub = connection.subscribe(topics);
    connection.publish("/topic", "Test my payload".getBytes(), QoS.AT_MOST_ONCE, false).await();
    byte[] qoses = futSub.await();
    assertEquals(1, qoses.length);
    connection.disconnect().await();
  }

  @Test public void testSubscribe() throws IOException, InterruptedException {
    Client client = new Client("localhost", Server.PORT);
    client.connect();
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
      }
    });
    client.publish("/topic", "Test my payload".getBytes());
    client.close();
    client.shutdown();
    assertTrue(received);
  }

  @Test public void testCleanSession_maintainClientSubscriptions() {
    Client client = new Client("localhost", Server.PORT);
    client.connect(false);
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
      }
    });
    client.close();
    client.connect(false);
    client.publish("/topic", "Test my payload".getBytes());
    client.close();
    assertTrue(received);
    client.shutdown();
  }

  @Test public void testCleanSession_maintainClientSubscriptions_againstClientDestruction() {
    Client client = new Client("localhost", Server.PORT, "CLID_123");
    client.connect(false);
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
      }
    });
    client.close();
    client.shutdown();
    client = new Client("localhost", Server.PORT, "CLID_123");
    client.register("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
      }
    });
    client.connect(false);
    client.publish("/topic", "Test my payload".getBytes());
    client.close();
    assertTrue(received);
    client.shutdown();
  }

  /**
     * Check that after a client has connected with clean session false, subscribed
     * to some topic and exited, if it reconnect with clean session true, the server
     * correctly cleanup every previous subscription
     */
  @Test public void testCleanSession_correctlyClientSubscriptions() {
    Client client = new Client("localhost", Server.PORT, "CLID_123");
    client.connect(false);
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
      }
    });
    client.close();
    client.shutdown();
    client = new Client("localhost", Server.PORT, "CLID_123");
    client.register("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
      }
    });
    client.connect(true);
    client.publish("/topic", "Test my payload".getBytes());
    client.close();
    assertFalse(received);
    client.shutdown();
  }

  @Test public void testCleanSession_maintainClientSubscriptions_withServerRestart() throws IOException, InterruptedException {
    final CountDownLatch barrier = new CountDownLatch(1);
    Client client = new Client("localhost", Server.PORT, "CLID_123");
    client.connect(false);
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
        barrier.countDown();
      }
    });
    assertFalse(barrier.await(1, TimeUnit.SECONDS));
    client.close();
    client.shutdown();
    server.stopServer();
    server.startServer();
    final CountDownLatch barrier2 = new CountDownLatch(1);
    client = new Client("localhost", Server.PORT, "CLID_123");
    client.register("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
        barrier2.countDown();
      }
    });
    client.connect(false);
    client.publish("/topic", "Test my payload".getBytes());
    client.close();
    assertTrue(barrier2.await(1, TimeUnit.SECONDS));
    assertTrue(received);
    client.shutdown();
  }

  @Test public void testRetain_maintainMessage_againstClientDestruction() throws InterruptedException {
    final CountDownLatch barrier = new CountDownLatch(1);
    Client client = new Client("localhost", Server.PORT, "CLID_123");
    client.connect();
    client.publish("/topic", "Test my payload".getBytes(), true);
    client.close();
    client.shutdown();
    client = new Client("localhost", Server.PORT, "CLID_123");
    client.connect();
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
        barrier.countDown();
      }
    });
    client.close();
    barrier.await(1, TimeUnit.SECONDS);
    assertTrue(received);
    client.shutdown();
  }

  @Ignore public void testUnsubscribe_do_not_notify_anymore_same_session() throws InterruptedException {
    Client client = new Client("localhost", Server.PORT, "CLID_123");
    client.connect();
    final CountDownLatch barrier = new CountDownLatch(1);
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
        barrier.countDown();
      }
    });
    client.publish("/topic", "Test my payload".getBytes());
    boolean unlocked = barrier.await(1000, TimeUnit.MILLISECONDS);
    assertTrue(unlocked);
    assertTrue(received);
    received = false;
    client.unsubscribe("/topic");
    client.publish("/topic", "Test my payload".getBytes());
    assertFalse(received);
    client.close();
    client.shutdown();
  }

  @Test public void testUnsubscribe_do_not_notify_anymore_new_session() throws InterruptedException {
    Client client = new Client("localhost", Server.PORT, "CLID_123");
    client.connect();
    final CountDownLatch barrier = new CountDownLatch(1);
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
        barrier.countDown();
      }
    });
    client.publish("/topic", "Test my payload".getBytes());
    boolean unlocked = barrier.await(1000, TimeUnit.MILLISECONDS);
    assertTrue(unlocked);
    assertTrue(received);
    client.unsubscribe("/topic");
    client.close();
    client.shutdown();
    client = new Client("localhost", Server.PORT, "CLID_123");
    client.connect();
    received = false;
    final CountDownLatch barrier2 = new CountDownLatch(1);
    client.publish("/topic", "Test my payload".getBytes());
    unlocked = barrier2.await(1000, TimeUnit.MILLISECONDS);
    assertFalse(unlocked);
    assertFalse(received);
    client.close();
    client.shutdown();
  }

  @Test public void testPublishWithQoS1() throws IOException, InterruptedException {
    Client client = new Client("localhost", Server.PORT);
    client.connect();
    client.subscribe("/topic", new IPublishCallback() {
      public void published(String topic, byte[] message) {
        received = true;
      }
    });
    client.publish("/topic", "Test my payload".getBytes(), AbstractMessage.QOSType.LEAST_ONE, false);
    client.close();
    client.shutdown();
    assertTrue(received);
  }
}