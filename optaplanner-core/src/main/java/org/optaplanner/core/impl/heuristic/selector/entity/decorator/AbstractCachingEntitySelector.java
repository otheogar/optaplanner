package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public abstract class AbstractCachingEntitySelector extends AbstractEntitySelector implements SelectionCacheLifecycleListener {

    protected final EntitySelector childEntitySelector;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedEntityList = null;

    public AbstractCachingEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType) {
        this.childEntitySelector = childEntitySelector;
        this.cacheType = cacheType;
        if (childEntitySelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childEntitySelector (" + childEntitySelector
                    + ") with neverEnding (" + childEntitySelector.isNeverEnding() + ").");
        }
        phaseLifecycleSupport.addEventListener(childEntitySelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        phaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public EntitySelector getChildEntitySelector() {
        return childEntitySelector;
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        long childSize = childEntitySelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childEntitySelector (" + childEntitySelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedEntityList = new ArrayList<Object>((int) childSize);
        Iterators.addAll(cachedEntityList, childEntitySelector.iterator());
        logger.trace("    Created cachedEntityList: size ({}), entitySelector ({}).",
                cachedEntityList.size(), this);
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedEntityList = null;
    }

    public EntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    public boolean isCountable() {
        return true;
    }

    public long getSize() {
        return cachedEntityList.size();
    }

    public Iterator<Object> endingIterator() {
        return cachedEntityList.iterator();
    }

}
