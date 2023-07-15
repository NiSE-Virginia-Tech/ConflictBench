package com.alibaba.fescar.tm.api;
import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.tm.api.transaction.TransactionHook;
import com.alibaba.fescar.tm.api.transaction.TransactionHookManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Template of executing business logic with a global transaction.
 */
public class TransactionalTemplate {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalTemplate.class);

  /**
     * Execute object.
     *
     * @param business the business
     * @return the object
     * @throws ExecutionException the execution exception
     */
  public Object execute(TransactionalExecutor business) throws TransactionalExecutor.ExecutionException {
    GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();

<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/tm/src/main/java/com/alibaba/fescar/tm/api/TransactionalTemplate.java
    try {
      rs = business.execute();
    } catch (Throwable ex) {
      try {
        tx.rollback();
        throw new TransactionalExecutor.ExecutionException(tx, TransactionalExecutor.Code.RollbackDone, ex);
      } catch (TransactionException txe) {
        throw new TransactionalExecutor.ExecutionException(tx, txe, TransactionalExecutor.Code.RollbackFailure, ex);
      }
    }
=======

>>>>>>> Unknown file

    try {
      try {
        triggerBeforeBegin();
        tx.begin(business.timeout(), business.name());
        triggerAfterBegin();
      } catch (TransactionException txe) {
        throw new TransactionalExecutor.ExecutionException(tx, txe, TransactionalExecutor.Code.BeginFailure);
      }
      Object rs = null;
      try {
        rs = business.execute();
      } catch (Throwable ex) {
        try {
          triggerBeforeRollback();
          tx.rollback();
          triggerAfterRollback();
          throw new TransactionalExecutor.ExecutionException(tx, TransactionalExecutor.Code.RollbackDone, ex);
        } catch (TransactionException txe) {
          throw new TransactionalExecutor.ExecutionException(tx, txe, TransactionalExecutor.Code.RollbackFailure, ex);
        }
      }
      try {
        triggerBeforeCommit();
        tx.commit();
        triggerAfterCommit();
      } catch (TransactionException txe) {
        throw new TransactionalExecutor.ExecutionException(tx, txe, TransactionalExecutor.Code.CommitFailure);
      }
      return rs;
    }  finally 
<<<<<<< Unknown file: This is a bug in JDime.
=======
    {
      triggerAfterCompletion();
      cleanUp();
    }
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/tm/src/main/java/com/alibaba/fescar/tm/api/TransactionalTemplate.java
  }

  private void triggerBeforeBegin() {
    for (TransactionHook hook : getCurrentHooks()) {
      try {
        hook.beforeBegin();
      } catch (Exception e) {
        LOGGER.error("Failed execute beforeBegin in hook " + e.getMessage());
      }
    }
  }

  private void triggerAfterBegin() {
    for (TransactionHook hook : getCurrentHooks()) {
      try {
        hook.afterBegin();
      } catch (Exception e) {
        LOGGER.error("Failed execute afterBegin in hook " + e.getMessage());
      }
    }
  }

  private void triggerBeforeRollback() {
    for (TransactionHook hook : getCurrentHooks()) {
      try {
        hook.beforeRollback();
      } catch (Exception e) {
        LOGGER.error("Failed execute beforeRollback in hook " + e.getMessage());
      }
    }
  }

  private void triggerAfterRollback() {
    for (TransactionHook hook : getCurrentHooks()) {
      try {
        hook.afterRollback();
      } catch (Exception e) {
        LOGGER.error("Failed execute afterRollback in hook " + e.getMessage());
      }
    }
  }

  private void triggerBeforeCommit() {
    for (TransactionHook hook : getCurrentHooks()) {
      try {
        hook.beforeCommit();
      } catch (Exception e) {
        LOGGER.error("Failed execute beforeCommit in hook " + e.getMessage());
      }
    }
  }

  private void triggerAfterCommit() {
    for (TransactionHook hook : getCurrentHooks()) {
      try {
        hook.afterCommit();
      } catch (Exception e) {
        LOGGER.error("Failed execute afterCommit in hook " + e.getMessage());
      }
    }
  }

  private void triggerAfterCompletion() {
    for (TransactionHook hook : getCurrentHooks()) {
      try {
        hook.afterCompletion();
      } catch (Exception e) {
        LOGGER.error("Failed execute afterCompletion in hook " + e.getMessage());
      }
    }
  }

  private void cleanUp() {
    TransactionHookManager.clear();
  }

  private List<TransactionHook> getCurrentHooks() {
    return TransactionHookManager.getHooks();
  }
}