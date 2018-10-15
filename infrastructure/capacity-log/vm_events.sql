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

#standardSQL
SELECT
  inserted,
  deleted,
  preemptible,
  instance_id,
  instance_log.project_id as project_id,
  instance_log.zone AS zone,
  cpus AS cores,
  memory_mb AS memory_mb,
  tags,
  labels
FROM (
  SELECT
    inserted.insert_timestamp AS inserted,
    deleted.delete_timestamp AS deleted,
    inserted.preemptible AS preemptible,
    inserted.project_id AS project_id,
    inserted.instance_id AS instance_id,
    zone,
    machine_type,
    tags,
    labels
  FROM (SELECT instance_id, insert_timestamp, project_id, zone, machine_type, preemptible, ANY_VALUE(labels) as labels, ANY_VALUE(tags) as tags FROM ((
      SELECT
        timestamp AS insert_timestamp,
        resource.labels.instance_id AS instance_id,
        resource.labels.project_id AS project_id,
        resource.labels.zone AS zone,
        REGEXP_EXTRACT(protopayload_auditlog.request_instances_insert.machinetype,r"([^/]+)$") AS machine_type,
        protopayload_auditlog.request_instances_insert.scheduling.preemptible AS preemptible,
        ARRAY(
        SELECT
          STRUCT(label.key,
            label.value)
        FROM
          UNNEST(protopayload_auditlog.request_instances_insert.labels) AS label) AS labels,
        protopayload_auditlog.request_instances_insert.tags.tags AS tags
      FROM
        `gce_capacity_log.cloudaudit_googleapis_com_activity*`
      WHERE
        protopayload_auditlog.response_operation.operationtype = "insert"
        AND resource.type = "gce_instance")
    UNION ALL (
      SELECT
        insert_timestamp,
        CAST(instance_id AS STRING) AS instance_id,
        project_id,
        zone,
        machine_type,
        preemptible,
        ARRAY(
        SELECT
          STRUCT(label.key,
            label.value)
        FROM
          UNNEST(labels) AS label) AS labels,
        tags
      FROM
        `gce_capacity_log.initial_vm_inventory` )) GROUP BY insert_timestamp, instance_id, project_id, zone, machine_type, preemptible) AS inserted
  LEFT JOIN (
    SELECT
      timestamp AS delete_timestamp,
      resource.labels.instance_id AS instance_id,
      operation.first AS deleted
    FROM
      `gce_capacity_log.cloudaudit_googleapis_com_activity_*`
    WHERE
      protopayload_auditlog.response_operation.operationtype = "delete"
      AND resource.type = "gce_instance") AS deleted
  USING
    (instance_id)) AS instance_log
JOIN (
  SELECT
    DISTINCT name,
    memory_mb,
    cpus
  FROM
    `gce_capacity_log.machine_types`) AS machine_types
ON
  machine_types.name = instance_log.machine_type