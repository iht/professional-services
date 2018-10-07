/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pso.gcpcapacitylog.machinetypes;

import static com.google.cloud.pso.gcpcapacitylog.machinetypes.MachineTypeScannerConsumer.convertToBQRow;
import static junit.framework.TestCase.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import com.google.api.services.cloudresourcemanager.model.Project;
import com.google.api.services.compute.model.MachineType;

public class MachineTypesTest {

  MachineTypeRow machineTypeRow;
  Project project;
  MachineType machineType;
  Gson gson = new GsonBuilder().setPrettyPrinting().create();;


  @Before
  public void setUp() {
    project = new Project();
    project.setProjectId("exampleprojectname");

    machineType = new MachineType();
    machineType.setIsSharedCpu(true);
    machineType.setGuestCpus(1);
    machineType.setKind("compute#machineType");
    machineType.setMemoryMb(614);
    machineType.setDescription("1 vCPU (shared physical core) and 0.6 GB RAM");
    machineType.setMaximumPersistentDisks(16);
    machineType.setMaximumPersistentDisksSizeGb(3072L);
    machineType.setCreationTimestamp("1969-12-31T16:00:00.000-08:00");
    machineType.setName("f1-micro");

    machineTypeRow = new MachineTypeRow();

    machineTypeRow.isSharedCpu = true;
    machineTypeRow.guestCpus = 1;
    machineTypeRow.kind = "compute#machineType";
    machineTypeRow.memoryMb = 614;
    machineTypeRow.description = "1 vCPU (shared physical core) and 0.6 GB RAM";
    machineTypeRow.maximumPersistentDisks = 16;
    machineTypeRow.maximumPersistentDisksSizeGb = 3072L;
    machineTypeRow.creationTimestamp = "1969-12-31T16:00:00.000-08:00";
    machineTypeRow.name = "f1-micro";

  }

  @Test
  public void convertToBQRowJsonTest() {
    assertEquals( gson.toJson(machineTypeRow), gson.toJson(convertToBQRow(machineType)));
  }

  @Test
  public void getMachineTypesBQSchemaTest() throws IOException {
    String file = IOUtils.toString(
        this.getClass().getResourceAsStream("/schema/machine_types_schema.json"),"UTF-8");
    assertEquals( file, gson.toJson(MachineTypeRow.getBQSchema()));
  }
}