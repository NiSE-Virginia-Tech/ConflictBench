package com.lts.example.api;
import com.lts.example.support.MasterChangeListenerImpl;
import com.lts.example.support.TestJobRunner;
import com.lts.tasktracker.TaskTracker;

/**
 * @author Robert HG (254963746@qq.com) on 8/19/14.
 */
public class TaskTrackerTest {
  public static void main(String[] args) {
    final TaskTracker taskTracker = new TaskTracker();
    taskTracker.setJobRunnerClass(TestJobRunner.class);
    taskTracker.setRegistryAddress("zookeeper://127.0.0.1:2181");
    taskTracker.setNodeGroup("test_trade_TaskTracker");
    taskTracker.setClusterName("test_cluster");
    taskTracker.setWorkThreads(10);
    taskTracker.addMasterChangeListener(new MasterChangeListenerImpl());
    taskTracker.addConfig("lts.monitor.url", "http://localhost:9090/");
    taskTracker.addConfig("lts.remoting", "netty");
    taskTracker.start();
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override public void run() {
        taskTracker.stop();
      }
    }));
  }
}