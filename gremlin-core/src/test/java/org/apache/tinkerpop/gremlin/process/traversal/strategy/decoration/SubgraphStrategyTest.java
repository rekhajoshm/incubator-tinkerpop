/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process.traversal.strategy.decoration;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.AndStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.WhereStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.EdgeVertexStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.VertexStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.sideEffect.IdentityStep;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class SubgraphStrategyTest {

    @Test
    public void shouldAddFilterAfterVertex() {
        final SubgraphStrategy strategy = SubgraphStrategy.build().vertexCriterion(__.identity()).create();
        final Traversal t = __.inV();
        strategy.apply(t.asAdmin());
        final EdgeVertexStep edgeVertexStep = (EdgeVertexStep) t.asAdmin().getStartStep();
        assertEquals(WhereStep.class, edgeVertexStep.getNextStep().getClass());
        final WhereStep h = (WhereStep) t.asAdmin().getEndStep();
        assertEquals(1, h.getLocalChildren().size());
        assertThat(((DefaultGraphTraversal) h.getLocalChildren().get(0)).getEndStep(), CoreMatchers.instanceOf(IdentityStep.class));
    }

    @Test
    public void shouldAddFilterAfterEdge() {
        final SubgraphStrategy strategy = SubgraphStrategy.build().edgeCriterion(__.identity()).create();
        final Traversal t = __.inE();
        strategy.apply(t.asAdmin());
        final VertexStep vertexStep = (VertexStep) t.asAdmin().getStartStep();
        assertEquals(WhereStep.class, vertexStep.getNextStep().getClass());
        final WhereStep h = (WhereStep) t.asAdmin().getEndStep();
        assertEquals(1, h.getLocalChildren().size());
        assertThat(((DefaultGraphTraversal) h.getLocalChildren().get(0)).getEndStep(), CoreMatchers.instanceOf(IdentityStep.class));
    }

    @Test
    public void shouldAddBothFiltersAfterVertex() {
        final SubgraphStrategy strategy = SubgraphStrategy.build().edgeCriterion(__.identity()).vertexCriterion(__.identity()).create();
        final Traversal t = __.inE();
        strategy.apply(t.asAdmin());
        final VertexStep vertexStep = (VertexStep) t.asAdmin().getStartStep();
        assertEquals(WhereStep.class, vertexStep.getNextStep().getClass());
        final WhereStep h = (WhereStep) t.asAdmin().getEndStep();
        assertEquals(1, h.getLocalChildren().size());
        assertThat(((DefaultGraphTraversal) h.getLocalChildren().get(0)).getEndStep(), CoreMatchers.instanceOf(AndStep.class));
    }
}
