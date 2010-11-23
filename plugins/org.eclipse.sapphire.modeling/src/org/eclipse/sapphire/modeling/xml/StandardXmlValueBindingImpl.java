package org.eclipse.sapphire.modeling.xml;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;

public final class StandardXmlValueBindingImpl

    extends XmlValueBindingImpl
    
{
    private XmlPath path;
    private boolean collapseWhitespace;
    private boolean treatExistanceAsValue;
    private String valueWhenPresent;
    private String valueWhenNotPresent;
    private boolean removeNodeOnSetIfNull;
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element, property, params );
        
        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) element.resource() ).getXmlNamespaceResolver();
        
        final XmlBinding genericBindingAnnotation = property.getAnnotation( XmlBinding.class );
        
        if( genericBindingAnnotation != null )
        {
            this.path = new XmlPath( genericBindingAnnotation.path(), xmlNamespaceResolver );
            this.removeNodeOnSetIfNull = true;
        }
        else
        {
            final XmlValueBinding bindingAnnotation = property.getAnnotation( XmlValueBinding.class );
            
            if( bindingAnnotation != null )
            {
                this.path = new XmlPath( bindingAnnotation.path(), xmlNamespaceResolver );
                this.collapseWhitespace = bindingAnnotation.collapseWhitespace();
                this.removeNodeOnSetIfNull = bindingAnnotation.removeNodeOnSetIfNull();
                
                if( bindingAnnotation.mapExistanceToValue().length() > 0 )
                {
                    this.treatExistanceAsValue = true;
                    
                    final String directive = bindingAnnotation.mapExistanceToValue();
                    StringBuilder buf = new StringBuilder();
                    boolean escapeNextChar = false;
                    int separatorCount = 0;
                    
                    for( int i = 0, n = directive.length(); i < n; i++ )
                    {
                        final char ch = directive.charAt( i );
                        
                        if( escapeNextChar )
                        {
                            buf.append( ch );
                            escapeNextChar = false;
                        }
                        else if( ch == '\\' )
                        {
                            escapeNextChar = true;
                        }
                        else if( ch == ';' )
                        {
                            separatorCount++;
                            
                            this.valueWhenPresent = buf.toString();
                            buf = new StringBuilder();
                        }
                        else
                        {
                            buf.append( ch );
                        }
                    }
                    
                    if( separatorCount == 0 )
                    {
                        this.valueWhenPresent = buf.toString();
                        
                        // todo: report an error
                    }
                    else
                    {
                        this.valueWhenNotPresent = buf.toString();
                        
                        if( separatorCount > 1 )
                        {
                            // todo: report an error;
                        }
                    }
                }
            }
            else
            {
                this.path = new XmlPath( property.getName(), xmlNamespaceResolver );
            }
        }
    }

    @Override
    public String read()
    {
        String value = null;
        
        final XmlElement element = ( (XmlResource) element().resource() ).getXmlElement( false );
        
        if( element != null )
        {
            if( this.treatExistanceAsValue )
            {
                final boolean exists = ( element.getChildNode( this.path, false ) != null );
                value = ( exists ? this.valueWhenPresent : this.valueWhenNotPresent );
            }
            else if( this.path == null )
            {
                value = element.getText( this.collapseWhitespace );
            }
            else
            {
                value = element.getChildNodeText( this.path, this.collapseWhitespace );
            }
        }
        
        return value;
    }

    @Override
    public void write( final String value )
    {
        final XmlResource resource = ( (XmlResource) element().resource() );
        
        if( this.treatExistanceAsValue )
        {
            final boolean nodeShouldBePresent = this.valueWhenPresent.equals( value );
            
            if( nodeShouldBePresent )
            {
                resource.getXmlElement( true ).getChildNode( this.path, true );
            }
            else
            {
                final XmlElement element = resource.getXmlElement( false );
                
                if( element != null )
                {
                    element.removeChildNode( this.path );
                }
            }
        }
        else if( this.path == null )
        {
            resource.getXmlElement( true ).setText( value );
        }
        else
        {
            resource.getXmlElement( true ).setChildNodeText( this.path, value, this.removeNodeOnSetIfNull );
        }
    }

    @Override
    public XmlNode getXmlNode()
    {
        final XmlElement element = ( (XmlResource) element().resource() ).getXmlElement( false );
        
        if( element != null )
        {
            return element.getChildNode( this.path, false );
        }
        
        return null;
    }
    
}