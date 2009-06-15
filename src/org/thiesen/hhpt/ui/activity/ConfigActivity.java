/*
 * $ Id $
 * (c) Copyright 2009 freiheit.com technologies gmbh
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies gmbh. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies gmbh.
 *
 * Initial version by Marcus Thiesen (marcus.thiesen@freiheit.com)
 */
package org.thiesen.hhpt.ui.activity;

import org.thiesen.hhpt.beans.Carriers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class ConfigActivity extends CustomActivity {

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.config);  
        
        final RadioButton vodafoneButton =   (RadioButton) findViewById( R.id.radio_vodafone );
        final RadioButton tmobileButton =   (RadioButton) findViewById( R.id.radio_tmobile );
        final RadioButton o2Button  =   (RadioButton) findViewById( R.id.radio_o2 );
        final RadioButton eplusButton =   (RadioButton) findViewById( R.id.radio_eplus );
        
        final Button okButton = (Button)findViewById( R.id.okButton );
        
        final Carriers selectedCarrier = preferences().getSelectedCarrier();
       
        vodafoneButton.setChecked( selectedCarrier == Carriers.VODAFONE );
        tmobileButton.setChecked( selectedCarrier == Carriers.TMOBILE );
        o2Button.setChecked( selectedCarrier == Carriers.O2 );
        eplusButton.setChecked( selectedCarrier == Carriers.EPLUS );
        
        okButton.setOnClickListener( new View.OnClickListener() {
        
            public void onClick( final View v ) {
                
                if ( vodafoneButton.isChecked() ) {
                    preferences().setSelectedCarrier( Carriers.VODAFONE );
                }
                if ( tmobileButton.isChecked() ) {
                    preferences().setSelectedCarrier( Carriers.TMOBILE );
                }
                if ( o2Button.isChecked() ) {
                    preferences().setSelectedCarrier( Carriers.O2 );
                }
                if ( eplusButton.isChecked() ) {
                    preferences().setSelectedCarrier( Carriers.EPLUS );
                }
                
                finish();
                
                    
            }
        } );
        
        
        
    }
   
    
    
}
