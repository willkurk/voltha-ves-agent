/*
* Copyright 2018- Cisco
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package ves;

import config.Config;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class VesDispatcher {

    private static final Logger logger = LoggerFactory.getLogger("VesDispatcher");

    private String url;
    private String port;

    private Gson gson;

    private CloseableHttpClient httpClient;

    public VesDispatcher(String url, String port) {
        this.url = url;
        this.port = port;

        gson = new GsonBuilder().create();

        httpClient = HttpClients.createDefault();
    }

    public int sendEvent(List<VesBlock> blocks) {
        JsonObject root = new JsonObject();
        JsonObject event = new JsonObject();
        for (VesBlock block : blocks) {
            JsonElement element = gson.toJsonTree(block);
            event.add(block.getName(), element);
        }
        root.add("event", event);
        String json = root.toString();
        System.out.println(json);
        int code = 0;

        try {
            HttpPost httpPost = new HttpPost(url + ":" + port+ "/eventListener/v5");
            StringEntity input = new StringEntity(json);
            input.setContentType("application/json");
            httpPost.setEntity(input);
            CloseableHttpResponse response = httpClient.execute(httpPost);

            try {
                System.out.println(response.getStatusLine());
                code = response.getStatusLine().getStatusCode();
            } finally {
                response.close();
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Error during http post", e);
            logger.error(e.toString());
        } catch (IOException e) {
            logger.error("Error during http post", e);
            logger.error(e.toString());
        }

        return code;
    }
}
