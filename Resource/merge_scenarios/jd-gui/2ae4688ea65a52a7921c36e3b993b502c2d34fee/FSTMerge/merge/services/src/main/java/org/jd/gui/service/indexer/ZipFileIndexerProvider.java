/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.service.indexer; 

import org.jd.gui.api.API; 
import org.jd.gui.api.model.Container; 
import org.jd.gui.api.model.Indexes; 
import org.jd.gui.spi.Indexer; 

public  class  ZipFileIndexerProvider  extends AbstractIndexerProvider {
	

    <<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647844962348/fstmerge_var1_7108736533575183763
@Override public String[] getSelectors() { return appendSelectors("*:file:*.zip", "*:file:*.jar", "*:file:*.war", "*:file:*.ear", "*:file:*.aar", "*:file:*.jmod", "*:file:*.kar"); }
=======
@Override public String[] getSelectors() { return appendSelectors("*:file:*.zip", "*:file:*.jar", "*:file:*.war", "*:file:*.ear", "*:file:*.aar"); }
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647844962348/fstmerge_var2_4979442207149184191


	

    @Override
    public void index(API api, Container.Entry entry, Indexes indexes) {
        for (Container.Entry e : entry.getChildren()) {
            if (e.isDirectory()) {
                index(api, e, indexes);
            } else {
                Indexer indexer = api.getIndexer(e);

                if (indexer != null) {
                    indexer.index(api, e, indexes);
                }
            }
        }
    }



}
