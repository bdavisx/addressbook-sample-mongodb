package nl.enovation.addressbook.cqrs.query.repositories;

import java.util.List;

import nl.enovation.addressbook.cqrs.command.CreateContactCommand;
import nl.enovation.addressbook.cqrs.commandhandler.ContactCommandHandler;
import nl.enovation.addressbook.cqrs.domain.Contact;
import nl.enovation.addressbook.cqrs.query.ContactEntry;

import org.axonframework.domain.AggregateIdentifier;
import org.axonframework.domain.UUIDAggregateIdentifier;
import org.axonframework.repository.Repository;
import org.axonframework.unitofwork.UnitOfWork;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/persistence-infrastructure-context.xml",
                                    "classpath:/META-INF/spring/cqrs-infrastructure-context.xml", "classpath:/META-INF/spring/contacts-context.xml" })

public class ContactQueryRepositoryTest {

    @Autowired
    private ContactQueryRepository contactQueryRepository;
    
    private String searchValue;

    @Test
    public void test() {
        contactQueryRepository.deleteAll();
            
        ContactEntry contactTest = new ContactEntry();
        contactTest.setFirstName("first");
        contactTest.setLastName("last");
        String randomString = "&*(@$&(⁹823jsf";
        
        contactQueryRepository.save(contactTest);
               
        searchValue = contactTest.getFirstName();
        List<ContactEntry> contacts = contactQueryRepository.searchForNames(searchValue);
        assertTrue(contacts.contains(contactTest));
        
        searchValue = contactTest.getLastName();
        contacts.clear();
        contacts = contactQueryRepository.searchForNames(searchValue);
        assertTrue(contacts.contains(contactTest));
        
        searchValue = randomString;
        contacts.clear();
        contacts = contactQueryRepository.searchForNames(searchValue);
        assertFalse(contacts.contains(contactTest));        
    }
}
