/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit.http; 

import java.io.UnsupportedEncodingException; 
import java.net.URLEncoder; 
import java.util.ArrayList; 
import java.util.List; 
import retrofit.http.client.Header; 
import retrofit.http.client.Request; 
import retrofit.http.mime.FormUrlEncodedTypedOutput; 
import retrofit.http.mime.MultipartTypedOutput; 
import retrofit.http.mime.TypedOutput; 

import static retrofit.http.RestMethodInfo.NO_BODY; 

/** Builds HTTP requests from Java method invocations. */
  class  RequestBuilder {
	
  

	

  

	
  

	
  

	
  

	

  

	

  /** Supply cached method metadata info. */
  

	

  /** Base API url. */
  

	

  /** Arguments from method invocation. */
  

	

  /** A list of custom headers. */
  

	

  /**
   * Construct a {@link Request} from the supplied information. You <strong>must</strong> call
   * {@link #methodInfo}, {@link #apiUrl}, {@link #args}, and {@link #headers} before invoking this
   * method.
   */
  

	

  /** Create the final relative URL by performing parameter replacement. */
  

	

  /** Create the request body using the method info and invocation arguments. */
  <<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647893530143/fstmerge_var1_460444515140987225
private TypedOutput buildBody() {
    switch (methodInfo.requestType) {
      case SIMPLE: {
        int bodyIndex = methodInfo.bodyIndex;
        if (bodyIndex == NO_BODY) {
          return null;
        }
        Object body = args[bodyIndex];
        if (body instanceof TypedOutput) {
          return (TypedOutput) body;
        } else {
          return converter.toBody(body);
        }
      }

      case FORM_URL_ENCODED: {
        FormUrlEncodedTypedOutput body = new FormUrlEncodedTypedOutput();
        String[] requestFormPair = methodInfo.requestFormPair;
        for (int i = 0; i < requestFormPair.length; i++) {
          String name = requestFormPair[i];
          if (name != null) {
            body.addField(name, String.valueOf(args[i]));
          }
        }
        return body;
      }

      case MULTIPART: {
        MultipartTypedOutput body = new MultipartTypedOutput();
        String[] requestMultipartPart = methodInfo.requestMultipartPart;
        for (int i = 0; i < requestMultipartPart.length; i++) {
          String name = requestMultipartPart[i];
          if (name != null) {
            Object value = args[i];
            if (value instanceof TypedOutput) {
              body.addPart(name, (TypedOutput) value);
            } else {
              body.addPart(name, converter.toBody(value));
            }
          }
        }
        return body;
      }

      default:
        throw new IllegalArgumentException("Unknown request type " + methodInfo.requestType);
    }
  }
=======
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647893530143/fstmerge_var2_2865796431348083467



}
