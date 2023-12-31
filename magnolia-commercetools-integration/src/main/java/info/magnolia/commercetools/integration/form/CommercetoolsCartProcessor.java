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
package info.magnolia.commercetools.integration.form;

import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.commercetools.integration.templating.CommercetoolsTemplatingFunctions;
import info.magnolia.context.Context;
import info.magnolia.context.WebContext;
import info.magnolia.module.form.processors.AbstractFormProcessor;
import info.magnolia.module.form.processors.FormProcessorFailedException;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.SetShippingMethod;
import io.sphere.sdk.models.SphereException;
import io.sphere.sdk.orders.OrderFromCartDraft;
import io.sphere.sdk.orders.PaymentState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.shippingmethods.ShippingMethod;

/**
 * Create order from cart.
 */
public class CommercetoolsCartProcessor extends AbstractFormProcessor {

    private static final Logger log = LoggerFactory.getLogger(CommercetoolsCartProcessor.class);

    private static final String CT_ACTION_PARAM = "ctAction";
    private static final String CT_SHIPPING_METHOD_PARAM = "ctShippingMethod";

    private static final String CT_ACTION_SET_SHIPPING = "setShipping";
    private static final String CT_ACTION_ORDER = "order";

    private final Provider<WebContext> webContextProvider;
    private final CommercetoolsTemplatingFunctions commercetoolsTemplatingFunctions;

    @Inject
    public CommercetoolsCartProcessor(Provider<WebContext> webContextProvider, CommercetoolsTemplatingFunctions commercetoolsTemplatingFunctions) {
        this.webContextProvider = webContextProvider;
        this.commercetoolsTemplatingFunctions = commercetoolsTemplatingFunctions;
    }

    @Override
    public void internalProcess(Node content, Map<String, Object> parameters) throws FormProcessorFailedException {
        final WebContext webContext = webContextProvider.get();
        final Cart cart = commercetoolsTemplatingFunctions.getCart();
        final String projectName = commercetoolsTemplatingFunctions.getProjectName();

        if (StringUtils.equals(parameters.get(CT_ACTION_PARAM).toString(), CT_ACTION_SET_SHIPPING)){
            final ShippingMethod selectedShippingMethod = commercetoolsTemplatingFunctions.getShippingMethod(parameters.get(CT_SHIPPING_METHOD_PARAM).toString());
            try {
                commercetoolsTemplatingFunctions.getProjectClient().execute(CartUpdateCommand.of(cart, SetShippingMethod.of(selectedShippingMethod))).toCompletableFuture().join();
            } catch (SphereException e) {
                log.error("Setting shipping method.", e);
                throw new FormProcessorFailedException(e.getMessage());
            }
        } else if (StringUtils.equals(parameters.get(CT_ACTION_PARAM).toString(), CT_ACTION_ORDER)) {
            try {
                commercetoolsTemplatingFunctions.getProjectClient().execute(OrderFromCartCreateCommand.of(OrderFromCartDraft.of(cart, null, PaymentState.BALANCE_DUE)))
                        .thenApplyAsync(order -> {
                            webContext.setAttribute(CommercetoolsServices.CT_LAST_ORDER_ID, order.getId(), Context.LOCAL_SCOPE);
                            webContext.removeAttribute(projectName + "_" + CommercetoolsServices.CT_CART_ID, Context.SESSION_SCOPE);
                            return null;
                        }).toCompletableFuture().join();
            } catch (SphereException e) {
                log.error("Order creation.", e);
                throw new FormProcessorFailedException(e.getMessage());
            }
        }
    }
}
