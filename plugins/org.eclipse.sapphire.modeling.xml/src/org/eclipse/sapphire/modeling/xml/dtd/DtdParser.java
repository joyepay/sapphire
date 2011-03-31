/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.xml.dtd;

import static org.eclipse.sapphire.modeling.util.MiscUtil.readTextContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;
import org.eclipse.sapphire.modeling.xml.dtd.internal.DtdParserImpl;
import org.eclipse.sapphire.modeling.xml.schema.UrlResolver;
import org.eclipse.sapphire.modeling.xml.schema.XmlDocumentSchema;

/**
 * Parses a DTD into XmlDocumentSchema representation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DtdParser
{
    public static XmlDocumentSchema parseFromString( final String dtd )
    {
        // The DTD parser is invoked twice. If entities are found in the first pass, then string substitution
        // is used to resolve them in the DTD and the DTD is re-parsed.
        
        try
        {
            String content = dtd;
            DtdParserImpl parser;
            
            parser = new DtdParserImpl( new StringReader( content ) );
            parser.Start();
            
            if( ! parser.entities.isEmpty() )
            {
                for( Map.Entry<String,String> entity : parser.entities.entrySet() )
                {
                    content = content.replace( "%" + entity.getKey() + ";", entity.getValue() );
                }
                
                parser = new DtdParserImpl( new StringReader( content ) );
                parser.Start();
            }
            
            return parser.schema.create();
        }
        catch( Exception e )
        {
            final String message = NLS.bind( Resources.parseFailed, "##string##" );
            final IStatus st = SapphireModelingFrameworkPlugin.createWarningStatus( message, e );
            SapphireModelingFrameworkPlugin.log( st );
            
            return ( new XmlDocumentSchema.Factory() ).create();
        }
    }

    public static XmlDocumentSchema parseFromUrl( final String baseLocation,
                                                  final String url )
    {
        URL dtdLocationUrl = null;
        
        try
        {
            dtdLocationUrl = new URL( UrlResolver.resolve( baseLocation, url ) );
        }
        catch( MalformedURLException e )
        {
            final String message = NLS.bind( Resources.parseFailed, url );
            final IStatus st = SapphireModelingFrameworkPlugin.createWarningStatus( message, e );
            SapphireModelingFrameworkPlugin.log( st );
        }
        
        if( dtdLocationUrl != null )
        {
            InputStream in = null;
            
            try
            {
                in = dtdLocationUrl.openStream();
                return parseFromString( readTextContent( in ) );
            }
            catch( Exception e )
            {
                final String message = NLS.bind( Resources.parseFailed, url );
                final IStatus st = SapphireModelingFrameworkPlugin.createWarningStatus( message, e );
                SapphireModelingFrameworkPlugin.log( st );
            }
            finally
            {
                if( in != null )
                {
                    try
                    {
                        in.close();
                    }
                    catch( IOException e ) {}
                }
            }
        }
        
        return ( new XmlDocumentSchema.Factory() ).create();
    }

    private static final class Resources extends NLS
    {
        public static String parseFailed;
        
        static
        {
            initializeMessages( DtdParser.class.getName(), Resources.class );
        }
    }

}