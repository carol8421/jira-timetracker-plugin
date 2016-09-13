/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.jira.updatenotifier;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.everit.jira.updatenotifier.json.JiraMarketplaceJSONDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.util.BuildUtilsInfo;
import com.google.gson.Gson;

/**
 * The update notifier.
 *
 */
public class UpdateNotificationUpdater {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateNotificationUpdater.class);

  private static final long ONE_DAY_IN_MILISEC = 86400000L;

  private int buildNumber;

  private UpdateNotifier updateNotifier;
  // TODO update interval handling

  public UpdateNotificationUpdater(final int buildNumber) {
    this.buildNumber = buildNumber;
  }

  public UpdateNotificationUpdater(final UpdateNotifier updateNotifier) {
    buildNumber = ComponentAccessor.getComponent(BuildUtilsInfo.class).getApplicationBuildNumber();
    this.updateNotifier = updateNotifier;
  }

  private synchronized void update() {
    if (!updateRequired()) {
      return;
    }
    updateNotifier.putLastUpdateTime(System.currentTimeMillis());
    HttpClient httpClient = new HttpClient();
    HttpMethod method = new GetMethod(
        "https://marketplace.atlassian.com/rest/2/addons/org.everit.jira.timetracker.plugin?application=jira&applicationBuild="
            + buildNumber + "&withVersion=true");
    method.addRequestHeader("accept", "application/json");
    String response;
    try {
      int statusCode = httpClient.executeMethod(method);
      if (statusCode != HttpStatus.SC_OK) {
        LOGGER.error("Update JTTP latest version failed. Status code is : " + statusCode);
        LOGGER.error("Response body: " + method.getResponseBodyAsString());
        return;
      }
      response = method.getResponseBodyAsString();
    } catch (IOException e) {
      LOGGER.error("Update JTTP latest version failed. ", e);
      return;
    }
    Gson gson = new Gson();
    JiraMarketplaceJSONDTO fromJson = gson.fromJson(response, JiraMarketplaceJSONDTO.class);
    String latestVersion = fromJson.getEmbedded().getVersion().getName();
    updateNotifier.putLatestVersion(latestVersion);
  }

  /**
   * Get the latest version from marketplace if the last update is older than one day. The new
   * version store in the {@link UpdateNotifier} class.
   *
   */
  public void updateLatestVersion() {
    if (updateRequired()) {
      update();
    }
  }

  private boolean updateRequired() {
    Long lastUpdateTimeInMilis = updateNotifier.getLastUpdateTime();
    return (lastUpdateTimeInMilis == null)
        || ((System.currentTimeMillis() - lastUpdateTimeInMilis.longValue()) > ONE_DAY_IN_MILISEC);
  }
}
