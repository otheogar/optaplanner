/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.solver.termination;

import org.junit.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TimeMillisSpentTerminationTest {

    @Test
    public void solveTermination() {
        Termination termination = new TimeMillisSpentTermination(1000L);
        DefaultSolverScope phaseScope = mock(DefaultSolverScope.class);

        when(phaseScope.calculateTimeMillisSpent()).thenReturn(0L);
        assertEquals(false, termination.isSolverTerminated(phaseScope));
        assertEquals(0.0, termination.calculateSolverTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculateTimeMillisSpent()).thenReturn(100L);
        assertEquals(false, termination.isSolverTerminated(phaseScope));
        assertEquals(0.1, termination.calculateSolverTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculateTimeMillisSpent()).thenReturn(500L);
        assertEquals(false, termination.isSolverTerminated(phaseScope));
        assertEquals(0.5, termination.calculateSolverTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculateTimeMillisSpent()).thenReturn(700L);
        assertEquals(false, termination.isSolverTerminated(phaseScope));
        assertEquals(0.7, termination.calculateSolverTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculateTimeMillisSpent()).thenReturn(1000L);
        assertEquals(true, termination.isSolverTerminated(phaseScope));
        assertEquals(1.0, termination.calculateSolverTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculateTimeMillisSpent()).thenReturn(1200L);
        assertEquals(true, termination.isSolverTerminated(phaseScope));
        assertEquals(1.0, termination.calculateSolverTimeGradient(phaseScope), 0.0);
    }

    @Test
    public void phaseTermination() {
        Termination termination = new TimeMillisSpentTermination(1000L);
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);

        when(phaseScope.calculatePhaseTimeMillisSpent()).thenReturn(0L);
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculatePhaseTimeMillisSpent()).thenReturn(100L);
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.1, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculatePhaseTimeMillisSpent()).thenReturn(500L);
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.5, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculatePhaseTimeMillisSpent()).thenReturn(700L);
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.7, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculatePhaseTimeMillisSpent()).thenReturn(1000L);
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.calculatePhaseTimeMillisSpent()).thenReturn(1200L);
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
    }

}
