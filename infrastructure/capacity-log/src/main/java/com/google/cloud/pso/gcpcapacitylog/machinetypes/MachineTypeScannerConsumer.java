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

import com.google.api.services.cloudresourcemanager.model.Project;
import com.google.api.services.compute.model.MachineType;
import com.google.cloud.pso.gcpcapacitylog.services.GCEHelper;
import com.google.common.flogger.FluentLogger;
import java.util.HashSet;
import java.util.Queue;

public class MachineTypeScannerConsumer implements Runnable {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private Queue<Project> queue;
  private HashSet<Object> machineTypeRows;

  public MachineTypeScannerConsumer(Queue<Project> queue, HashSet<Object> machineTypeRows) {
    this.queue = queue;
    this.machineTypeRows = machineTypeRows;
  }

  @Override
  public void run() {
    Project project;
    while (!queue.isEmpty()) {
      project = queue.poll();

      logger.atInfo().log("Processing machine types for project: " + project.getProjectId() + ". (" + queue.size() + " project remaining)");
      try {
        for (MachineType machineType : GCEHelper.getMachineTypesForProject(project)) {
          machineTypeRows.add(convertToBQRow(machineType));
        }
      } catch (Exception e) {
        logger.atSevere().log("Error while processing project: " + project.getProjectId() + " Putting project back in queue.", e);
        queue.add(project);
      }
    }
  }

  protected static MachineTypeRow convertToBQRow(MachineType machineType) {
    return new MachineTypeRow(
        machineType.getIsSharedCpu(),
        machineType.getKind(),
        machineType.getDescription(),
        machineType.getMemoryMb(),
        machineType.getMaximumPersistentDisks(),
        machineType.getMaximumPersistentDisksSizeGb(),
        machineType.getCreationTimestamp(),
        machineType.getName(),
        machineType.getGuestCpus()
    );
  }
}
