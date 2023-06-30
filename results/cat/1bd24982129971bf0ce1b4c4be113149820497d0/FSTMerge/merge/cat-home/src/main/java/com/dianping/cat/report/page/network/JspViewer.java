package com.dianping.cat.report.page.network; 

import com.dianping.cat.report.ReportPage; 
import org.unidal.web.mvc.view.BaseJspViewer; 

public  class  JspViewer  extends BaseJspViewer<ReportPage, Action, Context, Model> {
	
	@Override
	protected String getJspFilePath(Context ctx, Model model) {
		Action action = model.getAction();

		switch (action) {
		case NETWORK:
			return JspFile.NETWORK.getPath();
		case AGGREGATION:
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647843917672/fstmerge_var1_5074790302008225803
			return JspFile.DASHBOARD.getPath();
		case NETTOPOLOGY:
			return JspFile.NETTOPOLOGY.getPath();
=======
			return JspFile.AGGREGATION.getPath();
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647843917672/fstmerge_var2_1183113780613528719
		}

		throw new RuntimeException("Unknown action: " + action);
	}



}
