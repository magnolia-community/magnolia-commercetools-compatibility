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
package info.magnolia.commercetools.integration.app.detail.transformers;

import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;

import com.vaadin.v7.data.Item;

import io.sphere.sdk.models.LocalizedString;

/**
 * Transformer for LocalizedString.
 * 
 * @deprecated since 1.2, use {@link info.magnolia.commercetools.integration.app.detail.converters.LocalizedStringConverter} for {@link com.vaadin.v7.data.util.BeanItem} instead.
 */
@Deprecated
public class LocalizedStringTransformer extends BasicTransformer {
    public LocalizedStringTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class type, I18NAuthoringSupport i18NAuthoringSupport) {
        super(relatedFormItem, definition, type, i18NAuthoringSupport);
    }

    @Override
    public String readFromItem() {
        LocalizedString localizedString = ((LocalizedString) getOrCreateProperty(type).getValue());
        if (localizedString != null && localizedString.getLocales().contains(getLocale())) {
            return localizedString.get(getLocale());
        } else {
            return "";
        }
    }

    @Override
    protected String deriveLocaleAwareName(String baseName) {
        return baseName;
    }
}
