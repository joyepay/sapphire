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

package org.eclipse.sapphire.ui.renderers.swt;

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhindent;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhspan;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvindent;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdwhint;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glspacing;
import static org.eclipse.sapphire.ui.swt.renderer.SwtUtil.changeRadioButtonSelection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.EditFailedException;
import org.eclipse.sapphire.modeling.annotations.NamedValues;
import org.eclipse.sapphire.modeling.annotations.NamedValues.NamedValue;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.internal.binding.AbstractBinding;
import org.eclipse.sapphire.ui.swt.renderer.TextOverlayPainter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class NamedValuesPropertyEditorRenderer extends ValuePropertyEditorRenderer
{
    private Composite rootComposite;
    private boolean updating;
    private String defaultArbitraryValue;
    private Label overallLabelControl;
    private Label auxTextControl;
    private Button arbitraryValueRadioButton;
    private Text arbitraryValueTextField;
    private NamedValueLocal[] namedValues;
    private Button[] namedValuesRadioButtons;
    private List<Button> radioButtonGroup;
    
    public NamedValuesPropertyEditorRenderer( final SapphireRenderingContext context,
                                              final PropertyEditorPart part )
    {
        super( context, part );
    }
    
    protected void createContents( final Composite parent )
    {
        final PropertyEditorPart part = getPart();
        final ValueProperty property = (ValueProperty) part.property().definition();
        
        this.rootComposite = new Composite( parent, SWT.NONE )
        {
            @Override
            public void setEnabled( final boolean enabled )
            {
                super.setEnabled( enabled );
                
                NamedValuesPropertyEditorRenderer.this.overallLabelControl.setEnabled( enabled );
                
                if( NamedValuesPropertyEditorRenderer.this.auxTextControl != null )
                {
                    NamedValuesPropertyEditorRenderer.this.auxTextControl.setEnabled( enabled );
                }
                
                NamedValuesPropertyEditorRenderer.this.arbitraryValueRadioButton.setEnabled( enabled );
                NamedValuesPropertyEditorRenderer.this.arbitraryValueTextField.setEnabled( enabled && ( NamedValuesPropertyEditorRenderer.this.arbitraryValueRadioButton.getSelection() == true ) );
                
                for( Button b : NamedValuesPropertyEditorRenderer.this.namedValuesRadioButtons )
                {
                    b.setEnabled( enabled );
                }
            }
        };
        
        this.rootComposite.setLayout( glayout( 1, 0, 0 ) );
        
        final int baseIndent = part.getMarginLeft();
        this.rootComposite.setLayoutData( gdvindent( gdhindent( gdhspan( gdhfill(), 2 ), baseIndent ), 5 ) );
        
        final NamedValues namedValuesAnnotation = property.getAnnotation( NamedValues.class );
        final NamedValue[] namedValueAnnotations = namedValuesAnnotation.namedValues();
        
        this.namedValues = new NamedValueLocal[ namedValueAnnotations.length ];
        
        for( int i = 0, n = namedValueAnnotations.length; i < n; i++ )
        {
            final NamedValue x = namedValueAnnotations[ i ];
            
            final String namedValueLabel 
                = property.getLocalizationService().text( x.label(), CapitalizationType.FIRST_WORD_ONLY, true );
            
            this.namedValues[ i ] = new NamedValueLocal( x.value(), namedValueLabel );
        }
        
        this.updating = false;
        
        this.defaultArbitraryValue = namedValuesAnnotation.defaultArbitraryValue();
        this.defaultArbitraryValue = property.encodeKeywords( this.defaultArbitraryValue );
        
        final Composite composite = new Composite( this.rootComposite, SWT.NONE );
        composite.setLayoutData( gdhfill() );
        composite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );
        
        final PropertyEditorAssistDecorator decorator 
            = new PropertyEditorAssistDecorator( part, this.context, composite );
        
        decorator.control().setLayoutData( gdvalign( gd(), SWT.CENTER ) );
        decorator.addEditorControl( this.rootComposite );
        decorator.addEditorControl( composite );
        
        this.overallLabelControl = new Label( composite, SWT.WRAP );
        this.overallLabelControl.setLayoutData( gd() );
        this.overallLabelControl.setText( property.getLabel( true, CapitalizationType.FIRST_WORD_ONLY, true ) );
        decorator.addEditorControl( this.overallLabelControl );
        
        final SelectionListener selectionListener = new SelectionAdapter() 
        {
            @Override
            public void widgetSelected( final SelectionEvent event ) 
            {
                handleRadioButtonSelectedEvent( event );
            }
        };
        
        final Composite radioButtonsComposite = new Composite( this.rootComposite, SWT.NONE );
        radioButtonsComposite.setLayoutData( gdhindent( gdhfill(), 20 ) );
        radioButtonsComposite.setLayout( glayout( 2, 0, 0, 0, 0 ) );
        decorator.addEditorControl( radioButtonsComposite );
        
        this.radioButtonGroup = new ArrayList<Button>();
        
        final String arbitraryValueLabel 
            = property.getLocalizationService().text( namedValuesAnnotation.arbitraryValueLabel(), CapitalizationType.FIRST_WORD_ONLY, true ) + ":";
    
        this.arbitraryValueRadioButton = createRadioButton( radioButtonsComposite, arbitraryValueLabel );
        this.arbitraryValueRadioButton.setLayoutData( gd() );
        this.arbitraryValueRadioButton.addSelectionListener( selectionListener );
        this.radioButtonGroup.add( this.arbitraryValueRadioButton );
        decorator.addEditorControl( this.arbitraryValueRadioButton );

        this.arbitraryValueTextField = new Text( radioButtonsComposite, SWT.BORDER );
        this.arbitraryValueTextField.setLayoutData( gdwhint( gd(), 150 ) );
        decorator.addEditorControl( this.arbitraryValueTextField );
        
        this.arbitraryValueTextField.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( final ModifyEvent event )
                {
                    handleArbitraryValueTextFieldChangedEvent( event );
                }
            }
        );
        
        final TextOverlayPainter.Controller textOverlayPainterController = new TextOverlayPainter.Controller()
        {
            @Override
            public String getDefaultText()
            {
                return ( (Value<?>) property() ).getDefaultText();
            }
        };
    
        TextOverlayPainter.install( this.arbitraryValueTextField, textOverlayPainterController );
        
        this.namedValuesRadioButtons = new Button[ this.namedValues.length ];
        
        for( int i = 0; i < this.namedValues.length; i++ )
        {
            final Button rb = createRadioButton( radioButtonsComposite, this.namedValues[ i ].valueName );
            rb.addSelectionListener( selectionListener );
            decorator.addEditorControl( rb );
            this.namedValuesRadioButtons[ i ] = rb;
            this.radioButtonGroup.add( rb );
        }
        
        this.rootComposite.setData( "peditor", this );
        
        this.binding = new NamedValuesBinding( getPart(), this.context );
        
        this.rootComposite.setData( PropertyEditorPart.DATA_BINDING, this.binding );
        
        addControl( this.rootComposite );
    }
    
    private Button createRadioButton( final Composite parent, 
                                      final String label ) 
    {
        final Button b = new Button( parent, SWT.RADIO );
        b.setLayoutData( gdhspan( gd(), 2 ) );
        b.setText( label );
        
        return b;
    }
    
    private void handleRadioButtonSelectedEvent( final SelectionEvent event )
    {
        final Button b = (Button) event.getSource();
        
        if( b == this.arbitraryValueRadioButton )
        {
            setPropertyValue2( this.defaultArbitraryValue );
        }
        else
        {
            for( int i = 0; i < this.namedValuesRadioButtons.length; i++ )
            {
                if( b == this.namedValuesRadioButtons[ i ] )
                {
                    setPropertyValue2( this.namedValues[ i ].value );
                    break;
                }
            }
        }
    }
    
    private void handleArbitraryValueTextFieldChangedEvent( final ModifyEvent event )
    {
        if( this.updating )
        {
            return;
        }
        
        setPropertyValue2( this.arbitraryValueTextField.getText() );
    }
    
    private void update()
    {
        if( this.updating )
        {
            return;
        }
        
        this.updating = true;
        
        try
        {
            final Value<?> val = property();
            final String valueWithDefault = val.text( true );
            NamedValueLocal namedValue = null;

            if( valueWithDefault != null )
            {
                for( int i = 0; i < this.namedValues.length; i++ )
                {
                    final NamedValueLocal nm = this.namedValues[ i ];
                    
                    if( valueWithDefault.equals( nm.value ) )
                    {
                        namedValue = nm;
                        break;
                    }
                }
            }
            
            if( namedValue != null )
            {
                Button buttonToSelect = null;
                
                for( int i = 0; i < this.namedValues.length; i++ )
                {
                    if( namedValue == this.namedValues[ i ] )
                    {
                        buttonToSelect = this.namedValuesRadioButtons[ i ];
                        break;
                    }
                }

                changeRadioButtonSelection( this.radioButtonGroup, buttonToSelect );

                this.arbitraryValueTextField.setEnabled( false );
                this.arbitraryValueTextField.setText( MiscUtil.EMPTY_STRING );
            }
            else
            {
                changeRadioButtonSelection( this.radioButtonGroup, this.arbitraryValueRadioButton );
                
                this.arbitraryValueTextField.setEnabled( true );
                
                final String existingValue = this.arbitraryValueTextField.getText();
                String valueWithoutDefault = val.text( false );
                valueWithoutDefault = ( valueWithoutDefault == null ? "" : valueWithoutDefault );

                if( ! existingValue.equals( valueWithoutDefault ) )
                {
                    this.arbitraryValueTextField.setText( valueWithoutDefault );
                }
            }
        }
        finally
        {
            this.updating = false;
        }
    }
    
    private void setPropertyValue2( final String value )
    {
        try
        {
            property().write( value );
        }
        catch( Exception e )
        {
            final EditFailedException editFailedException = EditFailedException.findAsCause( e );
            
            if( editFailedException != null )
            {
                update();
            }
            else
            {
                SapphireUiFrameworkPlugin.log( e );
            }
        }
    }
    
    @Override
    protected void handleFocusReceivedEvent()
    {
        this.namedValuesRadioButtons[ 0 ].setFocus();
    }

    public static final class Factory extends PropertyEditorRendererFactory
    {
        @Override
        public boolean isApplicableTo( final PropertyEditorPart propertyEditorPart )
        {
            final PropertyDef property = propertyEditorPart.property().definition();
            return ( property instanceof ValueProperty && property.hasAnnotation( NamedValues.class ) );
        }
        
        @Override
        public PropertyEditorRenderer create( final SapphireRenderingContext context,
                                              final PropertyEditorPart part )
        {
            return new NamedValuesPropertyEditorRenderer( context, part );
        }
    }
    
    private static class NamedValueLocal
    {
        public String value;
        public final String valueName;
        
        public NamedValueLocal( final String value,
                                final String valueName )
        {
            this.value = value;
            this.valueName = valueName;
        }
    }
    
    private final class NamedValuesBinding extends AbstractBinding
    {
        public NamedValuesBinding( final PropertyEditorPart editor,
                                   final SapphireRenderingContext context )
        {
            super( editor, context, NamedValuesPropertyEditorRenderer.this.rootComposite );
        }
        
        @Override
        protected void doUpdateModel()
        {
        }
        
        @Override
        protected void doUpdateTarget()
        {
            update();
        }
    }
    
}
