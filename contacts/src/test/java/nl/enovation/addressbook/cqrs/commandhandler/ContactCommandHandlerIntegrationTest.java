package nl.enovation.addressbook.cqrs.commandhandler;

import nl.enovation.addressbook.cqrs.command.CreateContactCommand;
import nl.enovation.addressbook.cqrs.command.CreatePhoneNumberCommand;
import nl.enovation.addressbook.cqrs.command.RemoveContactCommand;
import nl.enovation.addressbook.cqrs.command.RemovePhoneNumberCommand;
import nl.enovation.addressbook.cqrs.command.UpdateContactCommand;
import nl.enovation.addressbook.cqrs.domain.Contact;
import nl.enovation.addressbook.cqrs.event.ContactCreatedEvent;
import nl.enovation.addressbook.cqrs.event.ContactRemovedEvent;
import nl.enovation.addressbook.cqrs.event.ContactUpdatedEvent;
import nl.enovation.addressbook.cqrs.event.PhoneNumberAddedEvent;
import nl.enovation.addressbook.cqrs.event.PhoneNumberRemovedEvent;
import nl.enovation.addressbook.cqrs.pojo.PhoneNumberEntry;
import nl.enovation.addressbook.cqrs.query.ContactEntry;

import org.axonframework.domain.AggregateIdentifier;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * @author Yorick Holkamp
 */

public class ContactCommandHandlerIntegrationTest {
    @Mock
    private ContactEntry mockContactEntry;

    @Mock
    private PhoneNumberEntry mockPhoneNumber;

    private FixtureConfiguration fixture;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        fixture = Fixtures.newGivenWhenThenFixture();
        ContactCommandHandler commandHandler = new ContactCommandHandler();
        commandHandler.setContactRepository(fixture.createGenericRepository(Contact.class));
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void testCreateContactCommandPipeline() {
        AggregateIdentifier identifier = fixture.getAggregateIdentifier();

        when(mockContactEntry.getIdentifier()).thenReturn(identifier.asString());
        CreateContactCommand createdEvent = new CreateContactCommand(identifier, mockContactEntry);

        // Check if our event will be properly fired
        fixture.given().when(createdEvent).expectEvents(new ContactCreatedEvent(identifier, mockContactEntry));
    }

    @Test
    public void testCreatePhoneNumberCommandPipeline() {
        AggregateIdentifier identifier = fixture.getAggregateIdentifier();
        when(mockContactEntry.getIdentifier()).thenReturn(identifier.asString());

        ContactCreatedEvent createdEvent = new ContactCreatedEvent(identifier, mockContactEntry);
        CreatePhoneNumberCommand givenCommand = new CreatePhoneNumberCommand(identifier, mockPhoneNumber);

        // Check if our event will be properly fired
        fixture.given(createdEvent).when(givenCommand).expectEvents(new PhoneNumberAddedEvent(identifier, mockPhoneNumber));
    }

    @Test
    public void testRemoveContactCommandPipeline() {
        AggregateIdentifier identifier = fixture.getAggregateIdentifier();
        RemoveContactCommand removeCommand = new RemoveContactCommand(identifier);

        ContactCreatedEvent createdEvent = new ContactCreatedEvent(identifier, mockContactEntry);

        fixture.given(createdEvent).when(removeCommand).expectEvents(new ContactRemovedEvent(identifier));
    }

    @Test
    public void testRemovePhoneNumberCommandPipeline() {
        // Set up the identifiers for our mocks
        AggregateIdentifier identifier = fixture.getAggregateIdentifier();
        when(mockContactEntry.getIdentifier()).thenReturn(identifier.asString());
        when(mockPhoneNumber.getPhoneNumber()).thenReturn("12345678");

        // Build two pre-existing events
        ContactCreatedEvent createdEvent = new ContactCreatedEvent(identifier, mockContactEntry);
        PhoneNumberAddedEvent phoneNumberCreatedEvent = new PhoneNumberAddedEvent(identifier, mockPhoneNumber);

        RemovePhoneNumberCommand givenCommand = new RemovePhoneNumberCommand(identifier, "12345678");

        // Check if our expected event will be properly fired
        fixture.given(createdEvent, phoneNumberCreatedEvent).when(givenCommand).expectEvents(new PhoneNumberRemovedEvent(identifier, "12345678"));
    }

    @Test
    public void testUpdateContactCommandPipeline() {
        AggregateIdentifier identifier = fixture.getAggregateIdentifier();
        when(mockContactEntry.getIdentifier()).thenReturn(identifier.asString());

        ContactCreatedEvent createdEvent = new ContactCreatedEvent(identifier, mockContactEntry);
        UpdateContactCommand updateCommand = new UpdateContactCommand(identifier, mockContactEntry);

        // Check if our event will be properly fired
        fixture.given(createdEvent).when(updateCommand).expectEvents(new ContactUpdatedEvent(identifier, mockContactEntry));
    }

}