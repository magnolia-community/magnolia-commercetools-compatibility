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
package info.magnolia.commercetools.integration.app.browser.availability;

import info.magnolia.commercetools.integration.app.item.CommercetoolsItemId;
import info.magnolia.ui.api.availability.AbstractAvailabilityRule;

/**
 * Rule that checks whether item is product or not.
 */
public class IsProductRule extends AbstractAvailabilityRule {

    @Override
    protected boolean isAvailableForItem(final Object itemId) {
        if (itemId != null && itemId instanceof CommercetoolsItemId) {
            return CommercetoolsItemId.ItemType.PRODUCT.equals(((CommercetoolsItemId) itemId).getType());
        }
        return false;
    }
}
