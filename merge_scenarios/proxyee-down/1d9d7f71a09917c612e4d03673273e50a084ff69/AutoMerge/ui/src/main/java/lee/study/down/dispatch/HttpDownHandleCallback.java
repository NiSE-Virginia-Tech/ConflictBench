package lee.study.down.dispatch;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import lee.study.down.content.ContentManager;
import lee.study.down.io.BdyZip;
import lee.study.down.model.ChunkInfo;
import lee.study.down.model.HttpDownInfo;
import lee.study.down.model.TaskInfo;
import lee.study.down.mvc.form.NewTaskForm;
import lee.study.down.mvc.form.WsForm;
import lee.study.down.mvc.ws.WsDataType;
import lee.study.down.util.FileUtil;

public class HttpDownHandleCallback implements HttpDownCallback {
  @Override public void onStart(HttpDownInfo httpDownInfo) throws Exception {
    ContentManager.DOWN.save();
    ContentManager.WS.sendMsg(ContentManager.DOWN.buildWsForm());
  }

  @Override public void onChunkStart(HttpDownInfo httpDownInfo, ChunkInfo chunkInfo) throws Exception {
  }

  @Override public void onProgress(HttpDownInfo httpDownInfo, ChunkInfo chunkInfo) throws Exception {
  }

  @Override public void onPause(HttpDownInfo httpDownInfo) throws Exception {
    ContentManager.WS.sendMsg(ContentManager.DOWN.buildWsForm());
  }

  @Override public void onContinue(HttpDownInfo httpDownInfo) throws Exception {
    ContentManager.WS.sendMsg(ContentManager.DOWN.buildWsForm());
  }

  @Override public void onError(HttpDownInfo httpDownInfo, Throwable cause) {
    ContentManager.DOWN.saveTask(httpDownInfo.getTaskInfo().getId());
    ContentManager.WS.sendMsg(ContentManager.DOWN.buildWsForm());
  }

  @Override public void onChunkError(HttpDownInfo httpDownInfo, ChunkInfo chunkInfo, Throwable cause) {
    ContentManager.WS.sendMsg();
  }

  @Override public void onChunkDone(HttpDownInfo httpDownInfo, ChunkInfo chunkInfo) {
    ContentManager.DOWN.saveTask(httpDownInfo.getTaskInfo().getId());
    ContentManager.WS.sendMsg(ContentManager.DOWN.buildWsForm());
  }

  @Override public void onMerge(HttpDownInfo httpDownInfo) throws Exception {
    ContentManager.DOWN.saveTask(httpDownInfo.getTaskInfo().getId());
    ContentManager.WS.sendMsg(ContentManager.DOWN.buildWsForm());
  }

  @Override public void onDone(HttpDownInfo httpDownInfo) throws Exception {
    TaskInfo taskInfo = httpDownInfo.getTaskInfo();
    ContentManager.DOWN.save();
    synchronized (taskInfo) {
      FileUtil.deleteIfExists(taskInfo.buildTaskRecordFilePath());
    }
    ContentManager.WS.sendMsg(ContentManager.DOWN.buildWsForm());
    NewTaskForm taskForm = NewTaskForm.parse(httpDownInfo);
    if (taskForm.isUnzip()) {
      if (BdyZip.isBdyZip(taskInfo.buildTaskFilePath())) {
        WsForm wsForm = new WsForm(WsDataType.UNZIP_NEW, new HashMap<String, String>() {
          {
            put("filePath", taskInfo.buildTaskFilePath());
            put("toPath", taskForm.getUnzipPath());
          }
        });
        ContentManager.WS.sendMsg(wsForm);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    File file = new File("c:/test.txt");
    try {
      file.createNewFile();
    } catch (IOException e) {
      System.out.println(e.getStackTrace()[0].getMethodName());
      e.printStackTrace();
    }
  }
}