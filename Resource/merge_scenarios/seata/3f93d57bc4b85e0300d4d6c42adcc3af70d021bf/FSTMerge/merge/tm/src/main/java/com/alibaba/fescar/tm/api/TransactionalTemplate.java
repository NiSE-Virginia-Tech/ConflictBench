/*
 *  Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
public  class  TransactionalTemplate {
	

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalTemplate.class);

	

    /**
     * Execute object.
     *
     * @param business the business
     * @return the object
     * @throws ExecutionException the execution exception
     */
    public Object execute(TransactionalExecutor business) throws TransactionalExecutor.ExecutionException {

        // 1. get or create a transaction
        GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();

        try {

            // 2. begin transaction
            try {
                triggerBeforeBegin();
                tx.begin(business.timeout(), business.name());
                triggerAfterBegin();
            } catch (TransactionException txe) {
                throw new TransactionalExecutor.ExecutionException(tx, txe,
                    TransactionalExecutor.Code.BeginFailure);

            }
            Object rs = null;
            try {

                // Do Your Business
                rs = business.execute();

            } catch (Throwable ex) {

                // 3. any business exception, rollback.
                try {
                    triggerBeforeRollback();
                    tx.rollback();
                    triggerAfterRollback();
                    // 3.1 Successfully rolled back
                    throw new TransactionalExecutor.ExecutionException(tx, TransactionalExecutor.Code.RollbackDone, ex);

                } catch (TransactionException txe) {
                    // 3.2 Failed to rollback
                    throw new TransactionalExecutor.ExecutionException(tx, txe,
                        TransactionalExecutor.Code.RollbackFailure, ex);

                }

            }
            // 4. everything is fine, commit.
            try {
                triggerBeforeCommit();
                tx.commit();
                triggerAfterCommit();
            } catch (TransactionException txe) {
                // 4.1 Failed to commit
                throw new TransactionalExecutor.ExecutionException(tx, txe,
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647844222020/fstmerge_var1_3127638340532683205
                    TransactionalExecutor.Code.RollbackFailure, ex);

            }

        }

        // 4. everything is fine, commit.
        try {
            tx.commit();

        } catch (TransactionException txe) {
            // 4.1 Failed to commit
            throw new TransactionalExecutor.ExecutionException(tx, txe,
                TransactionalExecutor.Code.CommitFailure);

=======
                    TransactionalExecutor.Code.CommitFailure);
            }

            return rs;
        } finally {
            //5. clear
            triggerAfterCompletion();
            cleanUp();
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647844222020/fstmerge_var2_7735573618231257999
        }
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
