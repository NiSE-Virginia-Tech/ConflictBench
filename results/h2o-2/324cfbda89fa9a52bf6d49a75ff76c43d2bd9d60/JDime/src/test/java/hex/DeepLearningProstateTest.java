package hex;
import hex.deeplearning.DeepLearning;
import hex.deeplearning.DeepLearningModel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import water.*;
import water.api.AUC;
import water.api.AUCData;
import water.exec.Env;
import water.exec.Exec2;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.fvec.ParseDataset2;
import water.util.Log;
import java.util.Random;

public class DeepLearningProstateTest extends TestUtil {
  @BeforeClass public static void stall() {
    stall_till_cloudsize(JUnitRunnerDebug.NODES);
  }

  public void runFraction(float fraction) {
    long seed = 0xDECAF;
    Random rng = new Random(seed);
    String[] datasets = new String[2];
    int[][] responses = new int[datasets.length][];
    datasets[0] = "smalldata/./logreg/prostate.csv";
    responses[0] = new int[] { 1, 2, 8 };
    datasets[1] = "smalldata/iris/iris.csv";
    responses[1] = new int[] { 4 };
    int testcount = 0;
    int count = 0;
    for (int i = 0; i < datasets.length; ++i) {
      String dataset = datasets[i];
      Key file = NFSFileVec.make(find_test_file(dataset));
      Frame frame = ParseDataset2.parse(Key.make(), new Key[] { file });
      Key vfile = NFSFileVec.make(find_test_file(dataset));
      Frame vframe = ParseDataset2.parse(Key.make(), new Key[] { vfile });
      try {
        for (boolean replicate : new boolean[] { true, false }) {
          for (boolean load_balance : new boolean[] { true, false }) {
            for (boolean shuffle : new boolean[] { true, false }) {
              for (boolean balance_classes : new boolean[] { true, false }) {
                for (int resp : responses[i]) {
                  for (DeepLearning.ClassSamplingMethod csm : new DeepLearning.ClassSamplingMethod[] { DeepLearning.ClassSamplingMethod.Stratified, DeepLearning.ClassSamplingMethod.Uniform }) {
                    for (int scoretraining : new int[] { 200, 20, 0 }) {
                      for (int scorevalidation : new int[] { 200, 20, 0 }) {
                        for (int vf : new int[] { 0, 1, -1 }) {
                          for (int n_folds : new int[] { 0, 2 }) {
                            if (n_folds != 0 && vf != 0) {
                              continue;
                            }
                            for (boolean keep_cv_splits : new boolean[] { false }) {
                              for (boolean override_with_best_model : new boolean[] { false, true }) {
                                for (int train_samples_per_iteration : new int[] { -2, -1, 0, rng.nextInt(200), 500 }) {
                                  DeepLearningModel model1 = null, model2 = null;
                                  Key dest = null, dest_tmp = null;
                                  count++;
                                  if (fraction < rng.nextFloat()) {
                                    continue;
                                  }
                                  try {
                                    Log.info("**************************)");
                                    Log.info("Starting test #" + count);
                                    Log.info("**************************)");
                                    final double epochs = 7 + rng.nextDouble() + rng.nextInt(4);
                                    final int[] hidden = new int[] { 1 + rng.nextInt(4), 1 + rng.nextInt(6) };
                                    Frame valid = null;
                                    if (vf == 1) {
                                      valid = frame;
                                    } else {
                                      if (vf == -1) {
                                        valid = vframe;
                                      }
                                    }
                                    dest_tmp = Key.make("first");
                                    {
                                      Log.info("Using seed: " + seed);
                                      DeepLearning p = new DeepLearning();
                                      p.checkpoint = null;
                                      p.destination_key = dest_tmp;
                                      p.source = frame;
                                      p.response = frame.vecs()[resp];
                                      p.validation = valid;
                                      p.hidden = hidden;
                                      if (i == 0 && resp == 2) {
                                        p.classification = false;
                                      }
                                      p.override_with_best_model = override_with_best_model;
                                      p.epochs = epochs;
                                      p.n_folds = n_folds;
                                      p.keep_cross_validation_splits = keep_cv_splits;
                                      p.seed = seed;
                                      p.train_samples_per_iteration = train_samples_per_iteration;
                                      p.force_load_balance = load_balance;
                                      p.replicate_training_data = replicate;
                                      p.shuffle_training_data = shuffle;
                                      p.score_training_samples = scoretraining;
                                      p.score_validation_samples = scorevalidation;
                                      p.classification_stop = -1;
                                      p.regression_stop = -1;
                                      p.balance_classes = balance_classes;
                                      p.quiet_mode = true;
                                      p.score_validation_sampling = csm;
                                      try {
                                        p.invoke();
                                      } catch (Throwable t) {
                                        t.printStackTrace();
                                        throw new RuntimeException(t);
                                      } finally {
                                        p.delete();
                                      }
                                      model1 = UKV.get(dest_tmp);

<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/src/test/java/hex/DeepLearningProstateTest.java
                                      Assert.assertTrue(p.train_samples_per_iteration <= 0 || model1.epoch_counter > epochs || Math.abs(model1.epoch_counter - epochs) / epochs < 0.1);
=======
                                      assert (((p.train_samples_per_iteration <= 0 || p.train_samples_per_iteration >= frame.numRows()) && model1.epoch_counter > epochs) || Math.abs(model1.epoch_counter - epochs) / epochs < 0.20);
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/src/test/java/hex/DeepLearningProstateTest.java

                                      if (n_folds != 0) {
                                        for (Key k : model1.get_params().xval_models) {
                                          DeepLearningModel cv_model = UKV.get(k);
                                          StringBuilder sb = new StringBuilder();
                                          cv_model.generateHTML("cv", sb);
                                          cv_model.delete_best_model();
                                          cv_model.delete();
                                        }
                                      }
                                    }
                                    dest = Key.make("restart");
                                    DeepLearning p = new DeepLearning();
                                    final DeepLearningModel tmp_model = UKV.get(dest_tmp);
                                    Assert.assertTrue(tmp_model.get_params().state == Job.JobState.DONE);
                                    Assert.assertTrue(tmp_model.model_info().get_processed_total() >= frame.numRows() * epochs);
                                    assert (tmp_model != null);
                                    p.checkpoint = dest_tmp;
                                    p.destination_key = dest;
                                    p.n_folds = 0;
                                    p.source = frame;
                                    p.validation = valid;
                                    p.response = frame.vecs()[resp];
                                    if (i == 0 && resp == 2) {
                                      p.classification = false;
                                    }
                                    p.override_with_best_model = override_with_best_model;
                                    p.epochs = epochs;
                                    p.seed = seed;
                                    p.train_samples_per_iteration = train_samples_per_iteration;
                                    try {
                                      p.invoke();
                                    } catch (Throwable t) {
                                      t.printStackTrace();
                                      throw new RuntimeException(t);
                                    } finally {
                                      p.delete();
                                    }
                                    model2 = UKV.get(dest);
                                    Assert.assertTrue(model2.get_params().state == Job.JobState.DONE);
                                    {
                                      StringBuilder sb = new StringBuilder();
                                      model2.generateHTML("test", sb);
                                    }
                                    if (model2.actual_best_model_key != null) {
                                      final DeepLearningModel best_model = UKV.get(model2.actual_best_model_key);
                                      Assert.assertTrue(best_model.get_params().state == Job.JobState.DONE);
                                      {
                                        StringBuilder sb = new StringBuilder();
                                        best_model.generateHTML("test", sb);
                                      }
                                      if (override_with_best_model) {
                                        Assert.assertEquals(best_model.error(), model2.error(), 0);
                                      }
                                    }
                                    if (valid == null) {
                                      valid = frame;
                                    }
                                    double threshold = 0;
                                    if (model2.isClassifier()) {
                                      Frame pred = null, pred2 = null;
                                      try {
                                        pred = model2.score(valid);
                                        StringBuilder sb = new StringBuilder();
                                        AUC auc = new AUC();
                                        double error = 0;
                                        if (model2.nclasses() == 2) {
                                          auc.actual = valid;
                                          assert (resp == 1);
                                          auc.vactual = valid.vecs()[resp];
                                          auc.predict = pred;
                                          auc.vpredict = pred.vecs()[2];
                                          auc.invoke();
                                          auc.toASCII(sb);
                                          AUCData aucd = auc.data();
                                          threshold = aucd.threshold();
                                          error = aucd.err();
                                          Log.info(sb);
                                          Assert.assertEquals(new ConfusionMatrix(aucd.cm()).err(), error, 1e-15);
                                          Assert.assertEquals(model2.calcError(valid, auc.vactual, pred, pred, "training", false, 0, null, auc, null), error, 1e-15);
                                        }
                                        double CMerrorOrig;
                                        {
                                          sb = new StringBuilder();
                                          water.api.ConfusionMatrix CM = new water.api.ConfusionMatrix();
                                          CM.actual = valid;
                                          CM.vactual = valid.vecs()[resp];
                                          CM.predict = pred;
                                          CM.vpredict = pred.vecs()[0];
                                          CM.invoke();
                                          sb.append("\n");
                                          sb.append("Threshold: " + "default\n");
                                          CM.toASCII(sb);
                                          Log.info(sb);
                                          CMerrorOrig = new ConfusionMatrix(CM.cm).err();
                                        }
                                        pred2 = new Frame(Key.make("pred2"), pred.names(), pred.vecs());
                                        pred2.delete_and_lock(null);
                                        pred2.unlock(null);
                                        if (model2.nclasses() == 2) {
                                          Env ev = Exec2.exec("pred2[,1]=pred2[,3]>=" + 0.5);
                                          try {
                                            pred2 = ev.popAry();
                                            String skey = ev.key();
                                            ev.subRef(pred2, skey);
                                          }  finally {
                                            if (ev != null) {
                                              ev.remove_and_unlock();
                                            }
                                          }
                                          water.api.ConfusionMatrix CM = new water.api.ConfusionMatrix();
                                          CM.actual = valid;
                                          CM.vactual = valid.vecs()[1];
                                          CM.predict = pred2;
                                          CM.vpredict = pred2.vecs()[0];
                                          CM.invoke();
                                          sb = new StringBuilder();
                                          sb.append("\n");
                                          sb.append("Threshold: " + 0.5 + "\n");
                                          CM.toASCII(sb);
                                          Log.info(sb);
                                          double threshErr = new ConfusionMatrix(CM.cm).err();
                                          Assert.assertEquals(threshErr, CMerrorOrig, 1e-15);
                                          ev = Exec2.exec("pred2[,1]=pred2[,3]>=" + threshold);
                                          try {
                                            pred2 = ev.popAry();
                                            String skey = ev.key();
                                            ev.subRef(pred2, skey);
                                          }  finally {
                                            if (ev != null) {
                                              ev.remove_and_unlock();
                                            }
                                          }
                                          CM = new water.api.ConfusionMatrix();
                                          CM.actual = valid;
                                          CM.vactual = valid.vecs()[1];
                                          CM.predict = pred2;
                                          CM.vpredict = pred2.vecs()[0];
                                          CM.invoke();
                                          sb = new StringBuilder();
                                          sb.append("\n");
                                          sb.append("Threshold: ").append(threshold).append("\n");
                                          CM.toASCII(sb);
                                          Log.info(sb);
                                          double threshErr2 = new ConfusionMatrix(CM.cm).err();
                                          Assert.assertEquals(threshErr2, error, 1e-15);
                                        }
                                      }  finally {
                                        if (pred != null) {
                                          pred.delete();
                                        }
                                        if (pred2 != null) {
                                          pred2.delete();
                                        }
                                      }
                                    }
                                    Log.info("Parameters combination " + count + ": PASS");
                                    testcount++;
                                  } catch (Throwable t) {
                                    t.printStackTrace();
                                    throw new RuntimeException(t);
                                  } finally {
                                    if (model1 != null) {
                                      model1.delete_xval_models();
                                      model1.delete_best_model();
                                      model1.delete();
                                    }
                                    if (model2 != null) {
                                      model2.delete_xval_models();
                                      model2.delete_best_model();
                                      model2.delete();
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }  finally {
        frame.delete();
        vframe.delete();
      }
    }
    Log.info("\n\n=============================================");
    Log.info("Tested " + testcount + " out of " + count + " parameter combinations.");
    Log.info("=============================================");
  }

  public static class Long extends DeepLearningProstateTest {
    @Test @Ignore public void run() throws Exception {
      runFraction(1f);
    }
  }

  public static class Mid extends DeepLearningProstateTest {
    @Test public void run() throws Exception {
      runFraction(0.01f);
    }
  }

  public static class Short extends DeepLearningProstateTest {
    @Test public void run() throws Exception {
      runFraction(0.001f);
    }
  }
}