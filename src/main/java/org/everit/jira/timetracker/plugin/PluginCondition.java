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
package org.everit.jira.timetracker.plugin;

import java.util.Collection;
import java.util.List;

import org.everit.jira.settings.TimeTrackerSettingsHelper;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;

/**
 * The JTTP Plugin condition class. You can set the plugin user groups at JTTP Global Settings admin
 * site. The setted groups stored at jira settings. If no group is setted everybody can use the
 * plugin.
 */
public class PluginCondition extends AbstractWebCondition {

  private TimeTrackerSettingsHelper settingsHelper;

  public PluginCondition(final TimeTrackerSettingsHelper settingsHelper) {
    this.settingsHelper = settingsHelper;
  }

  @Override
  public boolean shouldDisplay(final ApplicationUser user, final JiraHelper jiraHelper) {
    List<String> pluginGroups = settingsHelper.loadGlobalSettings().getPluginGroups();
    if (pluginGroups.isEmpty()) {
      return true;
    }
    Collection<String> groupNamesForUser =
        ComponentAccessor.getGroupManager().getGroupNamesForUser(user);
    for (String pluginGroup : pluginGroups) {
      if (groupNamesForUser.contains(pluginGroup)) {
        return true;
      }
    }
    return false;
  }

}
