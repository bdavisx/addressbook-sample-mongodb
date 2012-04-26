/*
 * Copyright (c) 2010. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.sample.app.init;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.domain.AggregateIdentifier;
import org.axonframework.domain.UUIDAggregateIdentifier;
import org.axonframework.sample.app.api.AddressType;
import org.axonframework.sample.app.api.CreateContactCommand;
import org.axonframework.sample.app.api.RegisterAddressCommand;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Uses the sample api to create some sample content</p>
 *
 * @author Allard Buijze
 */
public class ContactGenerator implements ApplicationListener {

    private CommandBus commandBus;
    private AtomicBoolean initialized = new AtomicBoolean();

    public ContactGenerator(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (!initialized.get() && event instanceof ContextRefreshedEvent) {
            initializeData();
        }
    }
    
    public void initializeData() {
        if (initialized.compareAndSet(false, true)) {
        	AggregateIdentifier uuidAllard = createContact("Allard");
        	AggregateIdentifier uuidJettro = createContact("Jettro");

            RegisterAddressCommand registerPrivateAddressCommand = new RegisterAddressCommand();
            registerPrivateAddressCommand.setAddressType(AddressType.PRIVATE);
            registerPrivateAddressCommand.setCity("The Hague");
            registerPrivateAddressCommand.setContactId(uuidAllard);
            registerPrivateAddressCommand.setStreetAndNumber("AxonBoulevard 1");
            registerPrivateAddressCommand.setZipCode("1234AB");
            dispatchRegisterAddress(registerPrivateAddressCommand);

            RegisterAddressCommand registerWorkAddressCommand = new RegisterAddressCommand();
            registerWorkAddressCommand.setAddressType(AddressType.WORK);
            registerWorkAddressCommand.setCity("Amsterdam");
            registerWorkAddressCommand.setContactId(uuidAllard);
            registerWorkAddressCommand.setStreetAndNumber("JTeam avenue");
            registerWorkAddressCommand.setZipCode("1234AB");
            dispatchRegisterAddress(registerWorkAddressCommand);

            RegisterAddressCommand registerJettroAddressCommand = new RegisterAddressCommand();
            registerJettroAddressCommand.setAddressType(AddressType.PRIVATE);
            registerJettroAddressCommand.setCity("Rotterdam");
            registerJettroAddressCommand.setContactId(uuidJettro);
            registerJettroAddressCommand.setStreetAndNumber("Feyenoordlaan 010");
            registerJettroAddressCommand.setZipCode("3000AA");
            dispatchRegisterAddress(registerJettroAddressCommand);
        }
    }
    
    private AggregateIdentifier createContact(String longName) {
    	CreateContactCommand createContact = new CreateContactCommand();
        createContact.setNewContactName(longName);
        AggregateIdentifier uuidAllard = new UUIDAggregateIdentifier();
        createContact.setContactId(uuidAllard);
        commandBus.dispatch(createContact);
        
        FutureCallback<AggregateIdentifier> createContactCallback =
                new FutureCallback<AggregateIdentifier>();
        commandBus.dispatch(createContact, createContactCallback);
        AggregateIdentifier userIdentifier;
        try {
            userIdentifier = createContactCallback.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userIdentifier;
    }
    
    private AggregateIdentifier dispatchRegisterAddress(RegisterAddressCommand registerAddress) {
        FutureCallback<AggregateIdentifier> createContactCallback =
                new FutureCallback<AggregateIdentifier>();
        commandBus.dispatch(registerAddress, createContactCallback);
        AggregateIdentifier addressIdentifier;
        try {
            addressIdentifier = createContactCallback.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return addressIdentifier;
    }
}
