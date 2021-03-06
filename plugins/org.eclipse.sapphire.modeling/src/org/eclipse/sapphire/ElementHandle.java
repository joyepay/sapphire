/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [376531] Need ability to distinguish between switch among heterogeneous elements 
 ******************************************************************************/

package org.eclipse.sapphire;

import java.util.SortedSet;

import org.eclipse.sapphire.modeling.ElementPropertyBinding;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelPath.AllDescendentsSegment;
import org.eclipse.sapphire.modeling.ModelPath.PropertySegment;
import org.eclipse.sapphire.modeling.ModelPath.TypeFilterSegment;
import org.eclipse.sapphire.services.PossibleTypesService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ElementHandle<T extends Element> extends Property
{
    private T content;
    
    public ElementHandle( final Element element,
                          final ElementProperty property )
    {
        super( element, property );
    }
    
    @SuppressWarnings( "unchecked" )
    
    public <TX extends Element> ElementHandle<TX> typed( final Class<TX> type )
    {
        if( definition().getTypeClass() != type )
        {
            throw new IllegalArgumentException();
        }
        
        return (ElementHandle<TX>) this;
    }
    
    @Override
    public void refresh()
    {
        synchronized( root() )
        {
            assertNotDisposed();
    
            refreshContent( false );

            if( this.content != null )
            {
                this.content.refresh();
            }
            
            refreshEnablement( false );
            refreshValidation( false );
        }
    }
    
    private void refreshContent( final boolean onlyIfNotInitialized )
    {
        boolean initialized;
        
        synchronized( this )
        {
            initialized = ( ( this.initialization & CONTENT_INITIALIZED ) != 0 ); 
        }
        
        if( initialized || ( ! initialized && onlyIfNotInitialized ) )
        {
            final ElementPropertyBinding binding = binding();
            final Resource resourceAfter = binding.read();
            final boolean proceed;
            
            synchronized( this )
            {
                final Resource resourceBefore = ( this.content == null ? null : this.content.resource() );
                
                initialized = ( ( this.initialization & CONTENT_INITIALIZED ) != 0 );
                proceed = ( ! initialized || resourceBefore != resourceAfter );
            }
            
            if( proceed )
            {
                T contentBefore;
                T contentAfter = null;
                
                if( resourceAfter != null )
                {
                    final ElementType type = binding.type( resourceAfter );
                    contentAfter = type.instantiate( this, resourceAfter );
                }

                PropertyContentEvent event = null;
                
                synchronized( this )
                {
                    contentBefore = this.content;
                    this.content = contentAfter;
                    
                    if( initialized )
                    {
                        event = new PropertyContentEvent( this );
                    }
                    else
                    {
                        this.initialization |= CONTENT_INITIALIZED;
                    }
                }
                
                if( contentBefore != null )
                {
                    try
                    {
                        contentBefore.dispose();
                    }
                    catch( Exception e )
                    {
                        LoggingService.log( e );
                    }
                }
                
                broadcast( event );
            }
        }
    }
    
    @Override
    public ElementProperty definition()
    {
        return (ElementProperty) super.definition();
    }
    
    @Override
    protected ElementPropertyBinding binding()
    {
        return (ElementPropertyBinding) super.binding();
    }
    
    @Override
    public void attach( final Listener listener, final ModelPath path )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( path == null )
        {
            throw new IllegalArgumentException();
        }
        
        synchronized( root() )
        {
            assertNotDisposed();
            
            if( path.length() > 0 )
            {
                final ModelPath.Segment head = path.head();
                
                if( head instanceof AllDescendentsSegment || head instanceof PropertySegment || head instanceof TypeFilterSegment )
                {
                    if( ! ( definition() instanceof ImpliedElementProperty ) )
                    {
                        attach( new PropagationListener( listener, path ) );
                    }
                    
                    final Element element = content();
                    
                    if( element != null )
                    {
                        element.attach( listener, path );
                    }
                    
                    return;
                }
            }
            
            super.attach( listener, path );
        }
    }

    @Override
    public void detach( final Listener listener, final ModelPath path )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( path == null )
        {
            throw new IllegalArgumentException();
        }
        
        synchronized( root() )
        {
            if( path.length() > 0 )
            {
                final ModelPath.Segment head = path.head();
                
                if( head instanceof AllDescendentsSegment || head instanceof PropertySegment || head instanceof TypeFilterSegment )
                {
                    if( ! ( definition() instanceof ImpliedElementProperty ) )
                    {
                        detach( new PropagationListener( listener, path ) );
                    }
                    
                    final Element element = content();
                    
                    if( element != null )
                    {
                        element.detach( listener, path );
                    }
                    
                    return;
                }
            }

            super.detach( listener, path );
        }
    }

    public T content()
    {
        return content( false );
    }
    
    public T content( final boolean createIfNecessary )
    {
        return content( createIfNecessary, (ElementType) null );
    }
    
    public T content( final boolean createIfNecessary,
                      final ElementType type )
    {
        init();
        
        refreshContent( true );
        
        final SortedSet<ElementType> possible = service( PossibleTypesService.class ).types();
        
        if( type != null && ! possible.contains( type ) )
        {
            throw new IllegalArgumentException();
        }
        
        if( createIfNecessary )
        {
            ElementType t = type;
            
            if( t == null )
            {
                if( possible.size() > 1 )
                {
                    throw new IllegalArgumentException();
                }
                
                t = possible.first();
            }
            
            final boolean create;
            
            synchronized( this )
            {
                create = ( this.content == null || this.content.type() != t );
            }
            
            if( create )
            {
                binding().create( t );
                refresh();
                //initialize
            }
        }
        else
        {
            synchronized( this )
            {
                if( this.content != null && type != null && this.content.type() != type )
                {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        return this.content;
    }
    
    public <C extends Element> C content( final boolean createIfNecessary,
                                                final Class<C> cl )
    {
        ElementType type = null;
        
        if( cl != null )
        {
            type = ElementType.read( cl );
            
            if( type == null )
            {
                throw new IllegalArgumentException();
            }
        }
        
        return cl.cast( content( createIfNecessary, type ) );
    }
    
    @Override
    public boolean empty()
    {
        synchronized( root() )
        {
            init();
            refreshContent( true );
            
            if( definition() instanceof ImpliedElementProperty )
            {
                for( Property property : this.content.properties() )
                {
                    if( ! property.empty() )
                    {
                        return false;
                    }
                }
                
                return true;
            }
            else
            {
                return ( this.content == null );
            }
        }
    }

    @Override
    public void clear()
    {
        init();
        
        refreshContent( true );        
        
        if( definition() instanceof ImpliedElementProperty )
        {
            content().clear();
        }
        else
        {
            binding().remove();
            refresh();
        }
    }
    
    @Override
    public void copy( final Element source )
    {
        init();
        
        refreshContent( true );
        
        if( source == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( definition().isReadOnly() )
        {
            throw new UnsupportedOperationException();
        }
        
        final Property p = source.property( definition() );
        
        if( p instanceof ElementHandle<?> )
        {
            if( definition() instanceof ImpliedElementProperty )
            {
                if( p.definition() instanceof ImpliedElementProperty )
                {
                    content().copy( ( (ElementHandle<?>) p ).content() );
                }
            }
            else
            {
                if( p.definition() instanceof ElementProperty )
                {
                    final Element sourceChildElement = ( (ElementHandle<?>) p ).content();
                    
                    if( sourceChildElement == null )
                    {
                        clear();
                    }
                    else
                    {
                        final ElementType sourceChildElementType = sourceChildElement.type();
                        
                        if( service( PossibleTypesService.class ).types().contains( sourceChildElementType ) )
                        {
                            content( true, sourceChildElementType ).copy( sourceChildElement );
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toString()
    {
        final T content = content();
        return ( content == null ? "<null>" : content.toString() );
    }
    
    @Override
    protected void disposeOther()
    {
        if( this.content != null )
        {
            this.content.dispose();
            this.content = null;
        }
    }
    
    private static final class PropagationListener extends FilteredListener<PropertyContentEvent>
    {
        private final Listener listener;
        private final ModelPath path;
        
        public PropagationListener( final Listener listener, final ModelPath path )
        {
            this.listener = listener;
            this.path = path;
        }
        
        @Override
        public boolean equals( final Object obj )
        {
            if( obj instanceof PropagationListener )
            {
                final PropagationListener pl = (PropagationListener) obj;
                return this.listener.equals( pl.listener ) && this.path.equals( pl.path );
            }
            
            return false;
        }
        
        @Override
        public int hashCode()
        {
            return this.listener.hashCode() ^ this.path.hashCode();
        }
        
        @Override
        protected void handleTypedEvent( final PropertyContentEvent event )
        {
            final Element element = ( (ElementHandle<?>) event.property() ).content();
            
            if( element != null )
            {
                element.attach( this.listener, this.path );
            }
        }
    }

}
