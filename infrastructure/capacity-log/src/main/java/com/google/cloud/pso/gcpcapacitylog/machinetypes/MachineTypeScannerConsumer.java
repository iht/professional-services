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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

public class MachineTypeScannerConsumer implements Runnable {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private BlockingQueue<Project> queue;
  private HashSet<Object> machineTypeRows;

  // Retry logic
  private ArrayList<Project> retries = new ArrayList<>();
  private static final int MAX_RETRIES = 3;

  public MachineTypeScannerConsumer(BlockingQueue<Project> queue, HashSet<Object> machineTypeRows) {
    this.queue = queue;
    this.machineTypeRows = machineTypeRows;
  }

  @Override
  public void run() {
    Project project;
    while (!queue.isEmpty()) {
      project = queue.poll();

      ArrayList batch = new ArrayList();
      logger.atInfo().log("Processing machine types for project: " + project.getProjectId() + ". (" + queue.size() + " projects remaining)");
      try {
        for (MachineType machineType : GCEHelper.getMachineTypesForProject(project)) {
          batch.add(convertToBQRow(machineType));
        }
        machineTypeRows.addAll(batch);
      } catch (Exception e) {
        // Retry logic. If the project fails more than MAX_RETRIES ignore the project. Otherwise put it back in queue for a retry.
        retries.add(project);
        if(Collections.frequency(retries, project) < MAX_RETRIES) {
          logger.atWarning().log("Error while processing project: " + project.getProjectId() + " Putting project back in queue.", e);
          queue.add(project);
        } else {
          logger.atSevere().log("Error while processing project: " + project.getProjectId() + "  Ignoring project.", e);
        }
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
