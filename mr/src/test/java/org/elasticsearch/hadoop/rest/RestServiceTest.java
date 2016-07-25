/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.rest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.hadoop.PartitionDefinition;
import org.elasticsearch.hadoop.serialization.dto.Shard;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class RestServiceTest {

    private List<PartitionDefinition> pds;
    private PartitionDefinition pd1, pd2, pd3, pd4, pd5, pd6;

    @Before
    public void setup() {
        Map info = new LinkedHashMap();
        info.put("name", "1");
        info.put("http_address", "inet[/localhost:9200]");
        info.put("state", "STARTED");
        info.put("shard", 1);
        info.put("index", "index");
        info.put("relocating_node", "none");
        info.put("node", "1");
        info.put("primary", "true");

        Shard sh1 = new Shard(info);

        info.put("name", "2");
        info.put("shard", 2);

        Shard sh2 = new Shard(info);

        info.put("name", "3");
        info.put("shard", 3);

        Shard sh3 = new Shard(info);

        info.put("name", "4");
        info.put("shard", 4);

        Shard sh4 = new Shard(info);

        info.put("name", "5");
        info.put("shard", 5);

        Shard sh5 = new Shard(info);

        info.put("name", "6");
        info.put("shard", 6);

        Shard sh6 = new Shard(info);

        pd1 = new PartitionDefinition(sh1.getIndex(), sh1.getName(), null, null);
        pd2 = new PartitionDefinition(sh2.getIndex(), sh2.getName(), null, null);
        pd3 = new PartitionDefinition(sh3.getIndex(), sh3.getName(), null, null);
        pd4 = new PartitionDefinition(sh4.getIndex(), sh4.getName(), null, null);
        pd5 = new PartitionDefinition(sh5.getIndex(), sh5.getName(), null, null);
        pd6 = new PartitionDefinition(sh6.getIndex(), sh6.getName(), null, null);

        pds = Arrays.asList(pd1, pd2, pd3, pd4, pd5, pd6);
    }

    @Test
    public void testAssignmentOnlyOneTask() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 0, 1);
        assertThat(results.size(), is(6));
        assertEquals(pds, results);
    }

    @Test
    public void testAssignmentOptimalNumberOfTasks() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 1, 6);
        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(pd2));
    }

    @Test
    public void testAssignmentDividingTasks() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 0, 2);
        assertThat(results.size(), is(3));
        assertThat(results.get(0), is(pd1));
        assertThat(results.get(1), is(pd2));
        assertThat(results.get(2), is(pd3));
    }

    @Test
    public void testAssignmentRemainderTasksGroup1() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 0, 4);
        assertThat(results.size(), is(2));
        assertThat(results.get(0), is(pd1));
        assertThat(results.get(1), is(pd2));
    }

    @Test
    public void testAssignmentRemainderTasksGroup2() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 1, 4);
        assertThat(results.size(), is(2));
        assertThat(results.get(0), is(pd3));
        assertThat(results.get(1), is(pd4));
    }

    @Test
    public void testAssignmentRemainderTasksGroup3() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 2, 4);
        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(pd5));
    }

    @Test
    public void testAssignmentRemainderTasksGroup4() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 3, 4);
        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(pd6));
    }

    @Test
    public void testAssignmentRemainderTasksGroup11() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 0, 5);
        assertThat(results.size(), is(2));
        assertThat(results.get(0), is(pd1));
        assertThat(results.get(1), is(pd2));
    }

    @Test
    public void testAssignmentRemainderTasksGroup12() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 3, 5);
        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(pd5));
    }

    @Test
    public void testAssignmentMoreTasksThanNeeded() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 6, 7);
        assertThat(results.size(), is(0));
    }
}