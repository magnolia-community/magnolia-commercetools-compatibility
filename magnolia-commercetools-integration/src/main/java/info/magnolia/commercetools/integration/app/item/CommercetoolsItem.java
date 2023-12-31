/*
 * This file Copyright (c) 2016-2018 Magnolia International Ltd.
 * (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made available under
 * the terms of the MIT License which accompanies this distribution, and
 * is available at https://opensource.org/license/mit
 *
 */
package info.magnolia.commercetools.integration.app.item;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.util.BeanItem;

/**
 * Base implementation for commercetools items.
 *
 * @param <BT> bean type
 */
public abstract class CommercetoolsItem<BT> extends BeanItem<BT> {

    private final String projectId;

    private final String parentId;

    private CommercetoolsItemId itemId;

    public CommercetoolsItem(final String projectId, final String parentId, final BT bean) {
        super(bean);
        if (StringUtils.isBlank(projectId)) {
            throw new IllegalArgumentException("Project id cannot be blank.");
        }
        this.projectId = projectId;
        this.parentId = parentId;
        itemId = createItemId();
    }

    /**
     * Creates new item id for the passed bean item.
     */
    protected abstract CommercetoolsItemId createItemId();

    public String getProjectId() {
        return projectId;
    }

    public String getParentId() {
        return parentId;
    }

    public CommercetoolsItemId getItemId() {
        return itemId;
    }
}
