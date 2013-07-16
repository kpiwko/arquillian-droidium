/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.arquillian.droidium.screenshooter.aspect;

import org.arquillian.droidium.container.api.AndroidDevice;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * It seems that Android device is injected into instance after observing
 * {@code org.jboss.arquillian.test.spi.event.suite.Before event}.
 *
 * We set our Android device for internal purposes into other variable
 * because we need it in our advice methods. We need to have it static
 * because it does not work otherwise.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@Aspect
public class AssertionAspect {

    @Inject
    private Instance<AndroidDevice> injectedDevice;

    private static AndroidDevice androidDevice = null;

    @Pointcut("execution(@org.junit.Test * *(..))")
    public void testMethodEntryPoint() {

    }

    @Before("testMethodEntryPoint()")
    public void executeBeforeEnteringTestMethod() {
    }

    @After("testMethodEntryPoint()")
    public void executeAfterEnteringTestMethod() {
        if (AssertionAspect.androidDevice != null) {
            System.out.println("android device not null & ready to take screenshots");
        }
    }

    public void getAndroidDevice(@Observes org.jboss.arquillian.test.spi.event.suite.Before event) {
        AssertionAspect.androidDevice = injectedDevice.get();
    }

}
