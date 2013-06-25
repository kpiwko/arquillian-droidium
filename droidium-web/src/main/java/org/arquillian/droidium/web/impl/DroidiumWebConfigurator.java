/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.droidium.web.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.arquillian.droidium.web.configuration.DroidiumWebConfiguration;
import org.arquillian.droidium.web.event.DroidiumWebConfigured;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

/**
 * Creator of configuration for Droidium for web.
 *
 * Observes:
 * <ul>
 * <li>{@link BeforeSuite}</li>
 * </ul>
 *
 * Creates:
 * <ul>
 * <li>{@link DroidiumWebConfiguration}</li>
 * </ul>
 *
 * Fires:
 * <ul>
 * <li>{@link DroidiumWebConfigured}</li>
 * </ul>
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class DroidiumWebConfigurator {

    private static final Logger logger = Logger.getLogger(DroidiumWebConfigurator.class.getName());

    public static final String ANDROID_DRONE_EXTENSION_NAME = "droidium-web";

    @Inject
    @SuiteScoped
    private InstanceProducer<DroidiumWebConfiguration> droidiumWebConfiguration;

    @Inject
    private Event<DroidiumWebConfigured> droidiumWebConfigured;

    public void configureDroidiumWeb(@Observes(precedence = 10) BeforeSuite event, ArquillianDescriptor descriptor) {

        logger.info("Configuring Android Drone for web");

        DroidiumWebConfiguration configuration = new DroidiumWebConfiguration();

        for (ExtensionDef extensionDef : descriptor.getExtensions()) {
            if (ANDROID_DRONE_EXTENSION_NAME.equals(extensionDef.getExtensionName())) {
                Map<String, String> properties = extensionDef.getExtensionProperties();
                if (properties.containsKey("androidServerApk")) {
                    configuration.setAndroidServerApk(new File(properties.get("androidServerApk")));
                }
            }
        }

        Validate.isReadable(configuration.getAndroidServerApk(), "You must provide a valid path to Android Server APK: "
                + configuration.getAndroidServerApk());

        File webdriverLog = configuration.getWebdriverLogFile();

        Validate.notNull(webdriverLog, "You must provide a valid path to Android Webdriver Monkey log file: "
                + configuration.getWebdriverLogFile());

        // create the log file if not present
        try {
            webdriverLog.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create Android Webdriver Monkey log file at "
                    + webdriverLog.getAbsolutePath(), e);
        }

        droidiumWebConfiguration.set(configuration);
        droidiumWebConfigured.fire(new DroidiumWebConfigured());
    }
}
