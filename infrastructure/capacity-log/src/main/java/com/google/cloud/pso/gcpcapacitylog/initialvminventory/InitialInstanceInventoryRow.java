/*
 * Copyright 2018 Google LLC
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

package com.google.cloud.pso.gcpcapacitylog.initialvminventory;

import com.google.api.services.compute.model.Tags;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Field.Mode;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.Schema;
import com.google.common.flogger.FluentLogger;
import com.google.gson.annotations.SerializedName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class InitialInstanceInventoryRow {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  @SerializedName("insert_timestamp")
  protected String timestamp;
  @SerializedName("instance_id")
  protected String instaceId;
  @SerializedName("project_id")
  protected String projectId;
  @SerializedName("zone")
  protected String zone;
  @SerializedName("machine_type")
  protected String machine_type;
  @SerializedName("preemptible")
  protected boolean preemptible;
  @SerializedName("tags")
  protected List<String> tags;
  @SerializedName("labels")
  protected List<KV> labels;

  public InitialInstanceInventoryRow(String timestamp, String instaceID,
      String zone,
      String machine_type, boolean preemptible, Tags tags, Map<String, String> labels) {
    this.timestamp = timestamp;
    this.instaceId = instaceID;
    try {
      this.projectId =  new URL(zone).getPath().split("/")[4];
    } catch (MalformedURLException e) {
      logger.at(Level.SEVERE).log("Could not parse URL for zone", e);
      projectId = "";
    }
    this.zone = zone.substring(zone.lastIndexOf("/") + 1);
    this.machine_type = machine_type.substring(machine_type.lastIndexOf("/") + 1);
    this.preemptible = preemptible;

    this.tags = new ArrayList<>();
    if (tags.getItems() != null) {
      for (String tag : tags.getItems()) {
        this.tags.add(tag);
      }
    }
    this.labels = new ArrayList<>();
    if (labels != null) {
      for (String key : labels.keySet()) {
        this.labels.add(new KV(key, labels.get(key)));
      }
    }
  }

  // For tests.
  protected InitialInstanceInventoryRow() {}

  public static Schema getBQSchema() {
	 Field f1 = Field.of("insert_timestamp", LegacySQLTypeName.TIMESTAMP);
	 Field f2 = Field.of("instance_id", LegacySQLTypeName.INTEGER);
	 Field f3 = Field.of("project_id", LegacySQLTypeName.STRING);
	 Field f4 = Field.of("zone", LegacySQLTypeName.STRING);
	 Field f5 = Field.of("machine_type", LegacySQLTypeName.STRING);
	 Field f6 = Field.of("preemptible", LegacySQLTypeName.BOOLEAN);
	 Field f7 = Field.newBuilder("tags", LegacySQLTypeName.STRING).setMode(Mode.REPEATED).build();
	 Field f8a = Field.of("key", LegacySQLTypeName.STRING);
	 Field f8b = Field.of("value", LegacySQLTypeName.STRING);
	 Field f8 = Field.newBuilder("labels", LegacySQLTypeName.RECORD, f8a, f8b).setMode(Mode.REPEATED).build();
	 return Schema.of(f1, f2, f3, f4, f5, f6, f7, f8);
 }

  protected static class KV {
	
	@SerializedName("key")
    String key;
	@SerializedName("value")
    String value;

    public KV(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }
  
}
