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
package org.everit.jira.reporting.plugin;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.everit.jira.timetracker.plugin.dto.ReportingSettingsValues;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * The implementation of the {@link ReportingPlugin}.
 */
public class ReportingPluginImpl implements ReportingPlugin, InitializingBean,
    DisposableBean, Serializable {

  private static final int DEFAULT_PAGE_SIZE = 20;

  /**
   * The plugin reporting settings is use Noworks.
   */
  private static final String JTTP_PLUGIN_REPORTING_SETTINGS_GROUPS = "reportingGroups";

  /**
   * The plugin repoting settings key prefix.
   */
  private static final String JTTP_PLUGIN_REPORTING_SETTINGS_KEY_PREFIX = "jttp_report";

  /**
   * The plugin reporting settings is use Noworks.
   */
  private static final String JTTP_PLUGIN_REPORTING_SETTINGS_PAGER_SIZE = "pagerSize";

  /**
   * Serial Version UID.
   */
  private static final long serialVersionUID = -3872710932298672883L;

  private int pageSize;

  private List<String> reportingGroups;

  /**
   * The plugin reporting setting form the settingsFactory.
   */
  private PluginSettings reportingSettings;

  /**
   * The plugin reporting setting values.
   */
  private ReportingSettingsValues reportingSettingsValues;

  /**
   * The PluginSettingsFactory.
   */
  private final PluginSettingsFactory settingsFactory;

  /**
   * Default constructor.
   */
  public ReportingPluginImpl(final PluginSettingsFactory settingsFactory) {
    this.settingsFactory = settingsFactory;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void destroy() throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public ReportingSettingsValues loadReportingSettings() {
    reportingSettings = settingsFactory
        .createSettingsForKey(JTTP_PLUGIN_REPORTING_SETTINGS_KEY_PREFIX);
    setReportingGroups();
    setPageSize();
    reportingSettingsValues =
        new ReportingSettingsValues().reportingGroups(reportingGroups).pageSize(pageSize);
    return reportingSettingsValues;
  }

  private void readObject(final java.io.ObjectInputStream stream) throws IOException,
      ClassNotFoundException {
    stream.close();
    throw new java.io.NotSerializableException(getClass().getName());
  }

  @Override
  public void saveReportingSettings(final ReportingSettingsValues reportingSettingsParameter) {
    reportingSettings = settingsFactory
        .createSettingsForKey(JTTP_PLUGIN_REPORTING_SETTINGS_KEY_PREFIX);
    reportingSettings.put(JTTP_PLUGIN_REPORTING_SETTINGS_KEY_PREFIX
        + JTTP_PLUGIN_REPORTING_SETTINGS_PAGER_SIZE,
        String.valueOf(reportingSettingsParameter.pageSize));
    reportingSettings.put(JTTP_PLUGIN_REPORTING_SETTINGS_KEY_PREFIX
        + JTTP_PLUGIN_REPORTING_SETTINGS_GROUPS, reportingSettingsParameter.reportingGroups);
  }

  private void setPageSize() {
    pageSize = DEFAULT_PAGE_SIZE;
    String pageSizeValue =
        (String) reportingSettings.get(JTTP_PLUGIN_REPORTING_SETTINGS_KEY_PREFIX
            + JTTP_PLUGIN_REPORTING_SETTINGS_PAGER_SIZE);
    if (pageSizeValue != null) {
      pageSize = Integer.parseInt(pageSizeValue);
    }
  }

  private void setReportingGroups() {
    List<String> reportingGroupsNames = (List<String>) reportingSettings
        .get(JTTP_PLUGIN_REPORTING_SETTINGS_KEY_PREFIX + JTTP_PLUGIN_REPORTING_SETTINGS_GROUPS);
    reportingGroups = new ArrayList<String>();
    if (reportingGroupsNames != null) {
      reportingGroups = reportingGroupsNames;
    }
  }

  private void writeObject(final java.io.ObjectOutputStream stream) throws IOException {
    stream.close();
    throw new java.io.NotSerializableException(getClass().getName());
  }
}