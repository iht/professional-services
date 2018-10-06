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

package com.google.cloud.pso.gcpcapacitylog.machinetypes;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.Schema;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class MachineTypeRow {

  @SerializedName("shared_cpu")
  Boolean isSharedCpu;

  @SerializedName("cpus")
  Integer guestCpus;

  @SerializedName("kind")
  String kind;

  @SerializedName("description")
  String description;


  @SerializedName("memory_mb")
  Integer memoryMb;

  @SerializedName("max_persistent_disks")
  Integer maximumPersistentDisks;

  @SerializedName("max_persistent_disk_size_gb")
  Long maximumPersistentDisksSizeGb;

  @SerializedName("creation_timestamp")
  String creationTimestamp;

  @SerializedName("name")
  String name;


  public MachineTypeRow(Boolean isSharedCpu, String kind, String description, 
      Integer memoryMb, Integer maximumPersistentDisks, Long maximumPersistentDisksSizeGb,
      String creationTimestamp, String name, Integer guestCpus) {

    this.isSharedCpu = isSharedCpu;
    this.guestCpus = guestCpus;
    this.kind = kind;
    this.description = description;
    this.memoryMb = memoryMb;
    this.maximumPersistentDisks = maximumPersistentDisks;
    this.maximumPersistentDisksSizeGb = maximumPersistentDisksSizeGb;
    this.creationTimestamp = creationTimestamp;
    this.name = name;
  }

  // For tests
  protected MachineTypeRow() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MachineTypeRow that = (MachineTypeRow) o;
    return Objects.equals(isSharedCpu, that.isSharedCpu) &&
        Objects.equals(guestCpus, that.guestCpus) &&
        Objects.equals(kind, that.kind) &&
        Objects.equals(description, that.description) &&
        Objects.equals(memoryMb, that.memoryMb) &&
        Objects.equals(maximumPersistentDisks, that.maximumPersistentDisks) &&
        Objects.equals(maximumPersistentDisksSizeGb, that.maximumPersistentDisksSizeGb) &&
        Objects.equals(creationTimestamp, that.creationTimestamp) &&
        Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {

    return Objects
        .hash(isSharedCpu, guestCpus, kind, description, memoryMb, maximumPersistentDisks,
            maximumPersistentDisksSizeGb, creationTimestamp, name);
  }
  
  public static Schema getBQSchema() {
     Field f1 = Field.of("shared_cpu", LegacySQLTypeName.BOOLEAN);
	 Field f2 = Field.of("cpus", LegacySQLTypeName.INTEGER);
	 Field f3 = Field.of("kind", LegacySQLTypeName.STRING);
	 Field f4 = Field.of("description", LegacySQLTypeName.STRING);
	 Field f5 = Field.of("memory_mb", LegacySQLTypeName.INTEGER);
	 Field f6 = Field.of("max_persistent_disks", LegacySQLTypeName.INTEGER);
	 Field f7 = Field.of("max_persistent_disk_size_gb", LegacySQLTypeName.INTEGER);
	 Field f8 = Field.of("creation_timestamp", LegacySQLTypeName.TIMESTAMP);
	 Field f9 = Field.of("name", LegacySQLTypeName.STRING);
	 return Schema.of(f1, f2, f3, f4, f5, f6, f7, f8, f9);
	
	
  }
}
