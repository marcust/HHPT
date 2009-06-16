/*
 * $ Id $
 * (c) Copyright 2009 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of HHPT.
 *
 *  HHPT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  HHPT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with HHPT.  If not, see <http://www.gnu.org/licenses/>.
 *
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
        
            public void onClick( @SuppressWarnings("unused") final View v ) {
                
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
