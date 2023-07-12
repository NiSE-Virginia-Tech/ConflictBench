package com.graphhopper.storage.index;
import com.graphhopper.routing.util.*;
import com.graphhopper.storage.Directory;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.storage.RAMDirectory;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;
import gnu.trove.set.hash.TIntHashSet;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Peter Karich
 */
public class LocationIndexTreeTest extends AbstractLocationIndexTester {
  protected final EncodingManager encodingManager = new EncodingManager("car");

  @Override public LocationIndexTree createIndex(Graph g, int resolution) {
    if (resolution < 0) {
      resolution = 500000;
    }
    return (LocationIndexTree) createIndexNoPrepare(g, resolution).prepareIndex();
  }

  public LocationIndexTree createIndexNoPrepare(Graph g, int resolution) {
    Directory dir = new RAMDirectory(location);
    LocationIndexTree tmpIDX = new LocationIndexTree(g, dir);
    tmpIDX.setResolution(resolution);
    return tmpIDX;
  }

  @Override public boolean hasEdgeSupport() {
    return true;
  }

  Graph createTestGraph(EncodingManager em) {
    Graph graph = createGHStorage(new RAMDirectory(), em, false);
    NodeAccess na = graph.getNodeAccess();
    na.setNode(0, 0.5, -0.5);
    na.setNode(1, -0.5, -0.5);
    na.setNode(2, -1, -1);
    na.setNode(3, -0.4, 0.9);
    na.setNode(4, -0.6, 1.6);
    graph.edge(0, 1, 1, true);
    graph.edge(0, 2, 1, true);
    graph.edge(0, 4, 1, true);
    graph.edge(1, 3, 1, true);
    graph.edge(2, 3, 1, true);
    graph.edge(2, 4, 1, true);
    graph.edge(3, 4, 1, true);
    return graph;
  }

  @Test public void testSnappedPointAndGeometry() {
    Graph graph = createTestGraph(encodingManager);
    LocationIndex index = createIndex(graph, -1);
    QueryResult res = index.findClosest(-0.4, 0.9, EdgeFilter.ALL_EDGES);
    assertTrue(res.isValid());
    assertEquals(new GHPoint(-0.4, 0.9), res.getSnappedPoint());
    res = index.findClosest(-0.6, 1.6, EdgeFilter.ALL_EDGES);
    assertTrue(res.isValid());
    assertEquals(new GHPoint(-0.6, 1.6), res.getSnappedPoint());
    res = index.findClosest(-0.2, 0.3, EdgeFilter.ALL_EDGES);
    assertTrue(res.isValid());
    assertEquals(26936, res.getQueryDistance(), 1);
    assertEquals(new GHPoint(-0.441624, 0.317259), res.getSnappedPoint());
  }

  @Test public void testInMemIndex() {
    Graph graph = createTestGraph(encodingManager);
    LocationIndexTree index = createIndexNoPrepare(graph, 50000);
    index.prepareAlgo();
    LocationIndexTree.InMemConstructionIndex inMemIndex = index.getPrepareInMemIndex();
    assertEquals(Helper.createTList(4, 4), index.getEntries());
    assertEquals(4, inMemIndex.getEntriesOf(0).size());
    assertEquals(10, inMemIndex.getEntriesOf(1).size());
    assertEquals(0, inMemIndex.getEntriesOf(2).size());
    index.dataAccess.create(10);
    inMemIndex.store(inMemIndex.root, LocationIndexTree.START_POINTER);
    TIntHashSet set = new TIntHashSet();
    set.add(0);
    TIntHashSet foundIds = new TIntHashSet();
    index.findNetworkEntries(0.5, -0.5, foundIds, 0);
    assertEquals(set, foundIds);
    set.add(1);
    set.add(2);
    foundIds.clear();
    index.findNetworkEntries(-0.5, -0.9, foundIds, 0);
    index.findNetworkEntries(-0.5, -0.9, foundIds, 1);
    assertEquals(set, foundIds);
    assertEquals(2, index.findID(-0.5, -0.9));
  }

  @Test public void testInMemIndex2() {
    Graph graph = createTestGraph2();
    LocationIndexTree index = createIndexNoPrepare(graph, 500);
    index.prepareAlgo();
    LocationIndexTree.InMemConstructionIndex inMemIndex = index.getPrepareInMemIndex();
    assertEquals(Helper.createTList(4, 4), index.getEntries());
    assertEquals(3, inMemIndex.getEntriesOf(0).size());
    assertEquals(5, inMemIndex.getEntriesOf(1).size());
    assertEquals(0, inMemIndex.getEntriesOf(2).size());
    index.dataAccess.create(10);
    inMemIndex.store(inMemIndex.root, LocationIndexTree.START_POINTER);
    assertEquals(2L, index.keyAlgo.encode(49.94653, 11.57114));
    assertEquals(3L, index.keyAlgo.encode(49.94653, 11.57214));
    assertEquals(6L, index.keyAlgo.encode(49.95053, 11.57714));
    assertEquals(6L, index.keyAlgo.encode(49.95053, 11.57814));
    assertEquals(1L, index.keyAlgo.encode(49.94553, 11.57214));
    assertEquals(12L, index.keyAlgo.encode(49.95153, 11.57714));
    TIntHashSet set = new TIntHashSet();
    set.addAll(Arrays.asList(28, 27, 26, 24, 23, 21, 19, 18, 16, 14, 6, 5, 4, 3, 2, 1, 0));
    TIntHashSet foundIds = new TIntHashSet();
    index.findNetworkEntries(49.950, 11.5732, foundIds, 0);
    assertEquals(set, foundIds);
  }

  @Test public void testInMemIndex3() {
    LocationIndexTree index = createIndexNoPrepare(createTestGraph(encodingManager), 10000);
    index.prepareAlgo();
    LocationIndexTree.InMemConstructionIndex inMemIndex = index.getPrepareInMemIndex();
    assertEquals(Helper.createTList(64, 4), index.getEntries());
    assertEquals(33, inMemIndex.getEntriesOf(0).size());
    assertEquals(69, inMemIndex.getEntriesOf(1).size());
    assertEquals(0, inMemIndex.getEntriesOf(2).size());
    index.dataAccess.create(1024);
    inMemIndex.store(inMemIndex.root, LocationIndexTree.START_POINTER);
    assertEquals(1 << 20, index.getCapacity());
    QueryResult res = index.findClosest(-.5, -.5, EdgeFilter.ALL_EDGES);
    assertEquals(1, res.getClosestNode());
  }

  @Test public void testReverseSpatialKey() {
    LocationIndexTree index = createIndex(createTestGraph(encodingManager), 200);
    assertEquals(Helper.createTList(64, 64, 64, 4), index.getEntries());
    String str44 = "00000000000000000000000000000000000000000000";
    assertEquals(str44 + "01010101111101111101", BitUtil.BIG.toBitString(index.createReverseKey(1.7, 0.099)));
  }

  @Test public void testMoreReal() {
    Graph graph = createGHStorage(new EncodingManager("CAR"));
    NodeAccess na = graph.getNodeAccess();
    na.setNode(1, 51.2492152, 9.4317166);
    na.setNode(0, 52, 9);
    na.setNode(2, 51.2, 9.4);
    na.setNode(3, 49, 10);
    graph.edge(1, 0, 1000, true);
    graph.edge(0, 2, 1000, true);
    graph.edge(0, 3, 1000, true).setWayGeometry(Helper.createPointList(51.21, 9.43));
    LocationIndex index = createIndex(graph, -1);
    assertEquals(2, index.findID(51.2, 9.4));
  }

  private Graph createTestGraphWithWayGeometry() {
    Graph graph = createGHStorage(encodingManager);
    NodeAccess na = graph.getNodeAccess();
    na.setNode(0, 0.5, -0.5);
    na.setNode(1, -0.5, -0.5);
    na.setNode(2, -1, -1);
    na.setNode(3, -0.4, 0.9);
    na.setNode(4, -0.6, 1.6);
    graph.edge(0, 1, 1, true);
    graph.edge(0, 2, 1, true);
    graph.edge(0, 4, 1, true).setWayGeometry(Helper.createPointList(1, 1));
    graph.edge(1, 3, 1, true).setWayGeometry(Helper.createPointList(0, 0));
    graph.edge(2, 3, 1, true);
    graph.edge(2, 4, 1, true);
    graph.edge(3, 4, 1, true);
    return graph;
  }

  @Test public void testWayGeometry() {
    Graph g = createTestGraphWithWayGeometry();
    LocationIndex index = createIndex(g, -1);
    assertEquals(1, index.findID(0, 0));
    assertEquals(1, index.findID(0, 0.1));
    assertEquals(1, index.findID(0.1, 0.1));
    assertEquals(1, index.findID(-0.5, -0.5));
  }

  @Test public void testFindingWayGeometry() {
    Graph g = createGHStorage(encodingManager);
    NodeAccess na = g.getNodeAccess();
    na.setNode(10, 51.2492152, 9.4317166);
    na.setNode(20, 52, 9);
    na.setNode(30, 51.2, 9.4);
    na.setNode(50, 49, 10);
    g.edge(20, 50, 1, true).setWayGeometry(Helper.createPointList(51.25, 9.43));
    g.edge(10, 20, 1, true);
    g.edge(20, 30, 1, true);
    LocationIndex index = createIndex(g, 2000);
    assertEquals(20, index.findID(51.25, 9.43));
  }

  @Test public void testEdgeFilter() {
    Graph graph = createTestGraph(encodingManager);
    LocationIndexTree index = createIndex(graph, -1);
    assertEquals(1, index.findClosest(-.6, -.6, EdgeFilter.ALL_EDGES).getClosestNode());
    assertEquals(2, index.findClosest(-.6, -.6, new EdgeFilter() {
      @Override public boolean accept(EdgeIteratorState iter) {
        return iter.getBaseNode() == 2 || iter.getAdjNode() == 2;
      }
    }).getClosestNode());
  }

  Graph createTestGraph2() {
    Graph graph = createGHStorage(new RAMDirectory(), encodingManager, false);
    NodeAccess na = graph.getNodeAccess();
    na.setNode(8, 49.94553, 11.57214);
    na.setNode(9, 49.94553, 11.57314);
    na.setNode(10, 49.94553, 11.57414);
    na.setNode(11, 49.94553, 11.57514);
    na.setNode(12, 49.94553, 11.57614);
    na.setNode(13, 49.94553, 11.57714);
    na.setNode(0, 49.94653, 11.57114);
    na.setNode(1, 49.94653, 11.57214);
    na.setNode(2, 49.94653, 11.57314);
    na.setNode(3, 49.94653, 11.57414);
    na.setNode(4, 49.94653, 11.57514);
    na.setNode(5, 49.94653, 11.57614);
    na.setNode(6, 49.94653, 11.57714);
    na.setNode(7, 49.94653, 11.57814);
    na.setNode(14, 49.94753, 11.57214);
    na.setNode(15, 49.94753, 11.57314);
    na.setNode(16, 49.94753, 11.57614);
    na.setNode(17, 49.94753, 11.57814);
    na.setNode(18, 49.94853, 11.57114);
    na.setNode(19, 49.94853, 11.57214);
    na.setNode(20, 49.94853, 11.57814);
    na.setNode(21, 49.94953, 11.57214);
    na.setNode(22, 49.94953, 11.57614);
    na.setNode(23, 49.95053, 11.57114);
    na.setNode(24, 49.95053, 11.57214);
    na.setNode(25, 49.95053, 11.57314);
    na.setNode(26, 49.95053, 11.57514);
    na.setNode(27, 49.95053, 11.57614);
    na.setNode(28, 49.95053, 11.57714);
    na.setNode(29, 49.95053, 11.57814);
    na.setNode(30, 49.95153, 11.57214);
    na.setNode(31, 49.95153, 11.57314);
    na.setNode(32, 49.95153, 11.57514);
    na.setNode(33, 49.95153, 11.57614);
    na.setNode(34, 49.95153, 11.57714);
    na.setNode(34, 49.95153, 11.57714);
    na.setNode(100, 49.941, 11.56614);
    na.setNode(101, 49.96053, 11.58814);
    graph.edge(0, 1, 10, true);
    graph.edge(1, 2, 10, true);
    graph.edge(2, 3, 10, true);
    graph.edge(3, 4, 10, true);
    graph.edge(4, 5, 10, true);
    graph.edge(6, 7, 10, true);
    graph.edge(8, 2, 10, true);
    graph.edge(9, 2, 10, true);
    graph.edge(10, 3, 10, true);
    graph.edge(11, 4, 10, true);
    graph.edge(12, 5, 10, true);
    graph.edge(13, 6, 10, true);
    graph.edge(1, 14, 10, true);
    graph.edge(2, 15, 10, true);
    graph.edge(5, 16, 10, true);
    graph.edge(14, 15, 10, true);
    graph.edge(16, 17, 10, true);
    graph.edge(16, 20, 10, true);
    graph.edge(16, 25, 10, true);
    graph.edge(18, 14, 10, true);
    graph.edge(18, 19, 10, true);
    graph.edge(18, 21, 10, true);
    graph.edge(19, 21, 10, true);
    graph.edge(21, 24, 10, true);
    graph.edge(23, 24, 10, true);
    graph.edge(24, 25, 10, true);
    graph.edge(26, 27, 10, true);
    graph.edge(27, 28, 10, true);
    graph.edge(28, 29, 10, true);
    graph.edge(24, 30, 10, true);
    graph.edge(24, 31, 10, true);
    graph.edge(26, 32, 10, true);
    graph.edge(27, 33, 10, true);
    graph.edge(28, 34, 10, true);
    return graph;
  }

  @Test public void testRMin() {
    Graph graph = createTestGraph(encodingManager);
    LocationIndexTree index = createIndex(graph, 50000);
    DistanceCalc distCalc = new DistancePlaneProjection();
    double rmin = index.calculateRMin(0.05, -0.3);
    double check = distCalc.calcDist(0.05, Math.abs(graph.getNodeAccess().getLon(2)) - index.getDeltaLon(), -0.3, -0.3);
    assertTrue((rmin - check) < 0.0001);
    double rmin2 = index.calculateRMin(0.05, -0.3, 1);
    double check2 = distCalc.calcDist(0.05, Math.abs(graph.getNodeAccess().getLat(0)), -0.3, -0.3);
    assertTrue((rmin2 - check2) < 0.0001);
    TIntHashSet points = new TIntHashSet();
    assertEquals(Double.MAX_VALUE, index.calcMinDistance(0.05, -0.3, points), 1e-1);
    points.add(0);
    points.add(1);
    assertEquals(54757.03, index.calcMinDistance(0.05, -0.3, points), 1e-1);
  }

  @Test public void testSearchWithFilter_issue318() {
    CarFlagEncoder carEncoder = new CarFlagEncoder();
    BikeFlagEncoder bikeEncoder = new BikeFlagEncoder();
    EncodingManager tmpEM = new EncodingManager(carEncoder, bikeEncoder);
    Graph graph = createGHStorage(new RAMDirectory(), tmpEM, false);
    NodeAccess na = graph.getNodeAccess();
    int MAX = 5;
    for (int latIdx = 0; latIdx < MAX; latIdx++) {
      for (int lonIdx = 0; lonIdx < MAX; lonIdx++) {
        int index = lonIdx * 10 + latIdx;
        na.setNode(index, 0.01 * latIdx, 0.01 * lonIdx);
        if (latIdx < MAX - 1) {
          graph.edge(index, index + 1, 1000, true);
        }
        if (lonIdx < MAX - 1) {
          graph.edge(index, index + 10, 1000, true);
        }
      }
    }
    AllEdgesIterator iter = graph.getAllEdges();
    while (iter.next()) {
      iter.setFlags(bikeEncoder.setAccess(iter.getFlags(), false, false));
    }
    for (EdgeIteratorState edge : Arrays.asList(GHUtility.getEdge(graph, 0, 1), GHUtility.getEdge(graph, 1, 2))) {
      edge.setFlags(bikeEncoder.setAccess(edge.getFlags(), true, true));
    }
    LocationIndexTree index = createIndexNoPrepare(graph, 500);
    index.prepareIndex();
    index.setMaxRegionSearch(8);
    EdgeFilter carFilter = new DefaultEdgeFilter(carEncoder, true, true);
    QueryResult qr = index.findClosest(0.03, 0.03, carFilter);
    assertTrue(qr.isValid());
    assertEquals(33, qr.getClosestNode());
    EdgeFilter bikeFilter = new DefaultEdgeFilter(bikeEncoder, true, true);
    qr = index.findClosest(0.03, 0.03, bikeFilter);
    assertTrue(qr.isValid());
    assertEquals(2, qr.getClosestNode());
  }
}