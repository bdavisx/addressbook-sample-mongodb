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

package nl.enovation.addressbook.cqrs.command;

import nl.enovation.addressbook.cqrs.query.ContactEntry;

import org.axonframework.domain.AggregateIdentifier;

/**
 * <p>
 * Updates the given contact using the properties of the given ContactEntry.
 * </p>
 * 
 * @author Jettro Coenradie
 */
public class UpdateContactCommand extends AbstractContactCrudCommand {

    public UpdateContactCommand(AggregateIdentifier identifier) {
        super(identifier);
    }

    public UpdateContactCommand(AggregateIdentifier identifier, ContactEntry contactEntry) {
        super(identifier, contactEntry);
    }
}
