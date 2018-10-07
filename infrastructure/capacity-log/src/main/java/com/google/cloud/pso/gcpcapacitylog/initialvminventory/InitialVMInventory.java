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

import com.google.api.services.cloudresourcemanager.model.Project;
import com.google.cloud.bigquery.JobStatistics;
import com.google.cloud.pso.gcpcapacitylog.services.BQHelper;
import com.google.cloud.pso.gcpcapacitylog.services.EmptyRowCollection;
import com.google.cloud.pso.gcpcapacitylog.services.GCEHelper;
import com.google.common.flogger.FluentLogger;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class InitialVMInventory {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final int THREAD_COUNT = 1;

  /**
   * This method scans a org for VMs and uploads an inventory of the current VMs for the table specificed in the input arguments.
   *
   * @param orgNumber the org number. Example: 143823328417
   * @param dataset the name of the dataset where the inventory should be written. Example: gce_capacity_log
   * @param tableName the table name where the inventory should be written. Example: initial_vm_inventory
   * @see InitialInstanceInventoryRow is the BigQuery datamodel
   */
  public static void writeVMInventorytoBQ(String projectId, String orgNumber, String dataset,
      String tableName)
      throws IOException, GeneralSecurityException, InterruptedException {

    BQHelper.deleteTable(projectId, dataset, tableName);
    BlockingQueue<Project> projects = GCEHelper.getProjectsForOrg(orgNumber);
    BlockingQueue<Object> initialVMInventoryQueue = new LinkedBlockingDeque<>();

    ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
    for (int i = 0; i < THREAD_COUNT; i++) {
      pool.execute(new InitialVMInventoryProducer(projects, initialVMInventoryQueue));
    }

    pool.shutdown();

    // While the producers are still running and the queue isnt empty, take objects from the queue and write to BQ
    while (!pool.isTerminated() || !initialVMInventoryQueue.isEmpty()) {
      JobStatistics statistics = null;

      Collection<Object> batch = new LinkedList<>();

      initialVMInventoryQueue.drainTo(batch);
      if (!batch.isEmpty()) {
        try {
          logger.atInfo().log("Writing " + batch.size() + " rows to BigQuery");
          statistics = BQHelper.insertIntoTable(projectId, dataset, tableName, InitialInstanceInventoryRow.getBQSchema(), batch);
          logger.atFine().log(statistics.toString());
        } catch (EmptyRowCollection e) {
          logger.atFinest().log("No input data supplied", e);
        }
      }

    }

    // Wait for 24 hours maximum until forceful termination of thread-pool.
    pool.awaitTermination(60 * 24, TimeUnit.MINUTES);

  }

}