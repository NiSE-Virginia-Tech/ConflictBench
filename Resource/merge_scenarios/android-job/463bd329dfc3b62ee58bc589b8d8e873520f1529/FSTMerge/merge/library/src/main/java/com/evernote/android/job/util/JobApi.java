/*
 * Copyright 2007-present Evernote Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.evernote.android.job.util; 

import android.app.AlarmManager; 
import android.app.Service; 
import android.app.job.JobScheduler; 
import android.content.Context; 
import android.content.Intent; 
import android.content.pm.ResolveInfo; 
import android.os.Build; 
import android.support.annotation.NonNull; 

import com.evernote.android.job.JobManager; 
import com.evernote.android.job.JobProxy; 
import com.evernote.android.job.gcm.JobProxyGcm; 
import com.evernote.android.job.v14.JobProxy14; 
import com.evernote.android.job.v19.JobProxy19; 
import com.evernote.android.job.v21.JobProxy21; 
import com.evernote.android.job.v21.PlatformJobService; 
import com.evernote.android.job.v24.JobProxy24; 
import com.google.android.gms.gcm.GcmNetworkManager; 

import java.util.List; 

/**
 * All available APIs.
 *
 * @author rwondratschek
 */
 enum  JobApi {
    /**
     * Uses the {@link JobScheduler} for scheduling jobs.
     */
    V_24(true, false) , 
    /**
     * Uses the {@link JobScheduler} for scheduling jobs.
     */
    V_24(true, false) , 
    /**
     * Uses the {@link JobScheduler} for scheduling jobs.
     */
    V_24(true, false) , 
    /**
     * Uses the {@link JobScheduler} for scheduling jobs.
     */
    V_24(true, false) , 
    /**
     * Uses the {@link JobScheduler} for scheduling jobs.
     */
    V_24(true, false); 

     

     
     

     

     

     

    <<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647922283068/fstmerge_var1_1315052009786584950
=======
public boolean isSupported(Context context) {
        switch (this) {
            case V_24:
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isServiceEnabled(context, PlatformJobService.class);
            case V_21:
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isServiceEnabled(context, PlatformJobService.class);
            case V_19:
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            case V_14:
                return true;
            case GCM:
                return GcmAvailableHelper.isGcmApiSupported(context);
            default:
                throw new IllegalStateException("not implemented");
        }
    }
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647922283068/fstmerge_var2_8239564395688262477
 

     

     

    private boolean isServiceEnabled(@NonNull Context context, @NonNull Class<? extends Service> clazz) {
        // on some rooted devices user can disable services
        try {
            Intent intent = new Intent(context, clazz);
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(intent, 0);
            return resolveInfos != null && !resolveInfos.isEmpty();
        } catch (Exception e) {
            return false;
        }
    } 

    /**
     * @deprecated Use {@link #getDefault(Context, boolean)} instead.
     */
     

    }
