/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.contacts.internal;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.samples.contacts.Contact;
import org.eclipse.sapphire.samples.contacts.ContactRepository;
import org.eclipse.sapphire.services.ImageService;
import org.eclipse.sapphire.services.ImageServiceData;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ContactRepositoryImageService extends ImageService
{
    private static final ImageServiceData IMG_PERSON = new ImageServiceData( ImageData.readFromClassLoader( Contact.class, "Contact.png" ) );
    private static final ImageServiceData IMG_PERSON_FADED = new ImageServiceData( ImageData.readFromClassLoader( Contact.class, "ContactFaded.png" ) );
    
    private Listener listener;
    
    @Override
    protected void initImageService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };
        
        context( Element.class ).attach( this.listener, "Contacts/EMail" );
    }

    @Override
    protected ImageServiceData compute()
    {
        boolean foundContactWithEMail = false;
        
        for( Contact contact : context( ContactRepository.class ).getContacts() )
        {
            if( contact.getEMail().content() != null )
            {
                foundContactWithEMail = true;
                break;
            }
        }
        
        return ( foundContactWithEMail ? IMG_PERSON : IMG_PERSON_FADED );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        context( Element.class ).detach( this.listener, "Contacts/EMail" );
    }
    
}
