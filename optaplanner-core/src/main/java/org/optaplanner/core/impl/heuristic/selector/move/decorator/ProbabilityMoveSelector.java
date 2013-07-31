/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.util.RandomUtils;

public class ProbabilityMoveSelector extends AbstractMoveSelector implements SelectionCacheLifecycleListener {

    protected final MoveSelector childMoveSelector;
    protected final SelectionCacheType cacheType;
    protected final SelectionProbabilityWeightFactory probabilityWeightFactory;

    protected NavigableMap<Double, Move> cachedMoveMap = null;
    protected double probabilityWeightTotal = -1.0;

    public ProbabilityMoveSelector(MoveSelector childMoveSelector, SelectionCacheType cacheType,
            SelectionProbabilityWeightFactory probabilityWeightFactory) {
        this.childMoveSelector = childMoveSelector;
        this.cacheType = cacheType;
        this.probabilityWeightFactory = probabilityWeightFactory;
        if (childMoveSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childMoveSelector (" + childMoveSelector
                    + ") with neverEnding (" + childMoveSelector.isNeverEnding() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(childMoveSelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        cachedMoveMap = new TreeMap<Double, Move>();
        ScoreDirector scoreDirector = solverScope.getScoreDirector();
        double probabilityWeightOffset = 0L;
        for (Move entity : childMoveSelector) {
            double probabilityWeight = probabilityWeightFactory.createProbabilityWeight(
                    scoreDirector, entity);
            cachedMoveMap.put(probabilityWeightOffset, entity);
            probabilityWeightOffset += probabilityWeight;
        }
        probabilityWeightTotal = probabilityWeightOffset;
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        probabilityWeightTotal = -1.0;
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return true;
    }

    public long getSize() {
        return cachedMoveMap.size();
    }

    public Iterator<Move> iterator() {
        return new Iterator<Move>() {
            public boolean hasNext() {
                return true;
            }

            public Move next() {
                double randomOffset = RandomUtils.nextDouble(workingRandom, probabilityWeightTotal);
                Map.Entry<Double, Move> entry = cachedMoveMap.floorEntry(randomOffset);
                // entry is never null because randomOffset < probabilityWeightTotal
                return entry.getValue();
            }

            public void remove() {
                throw new UnsupportedOperationException("The optional operation remove() is not supported.");
            }
        };
    }

    @Override
    public String toString() {
        return "Probability(" + childMoveSelector + ")";
    }

}