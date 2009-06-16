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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.thiesen.hhpt.beans.Carriers;
import org.thiesen.hhpt.shared.model.station.Station;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StationDetailsActivity extends CustomActivity {

    
    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.station_details);  
        final TextView titleView = (TextView) findViewById(R.id.title);
        final TextView feedback = (TextView) findViewById(R.id.feedback);
        final TextView employeeView = (TextView) findViewById(R.id.employee);
        
        final TextView targetText = (TextView) findViewById(R.id.edittext);
        final TextView targetTime = (TextView) findViewById(R.id.edittime);
        
        targetText.setText( preferences().getDefaultTarget() );
        
        final Calendar currentTime = Calendar.getInstance();
        currentTime.add( Calendar.MINUTE, 5 );
        
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        
        targetTime.setText( format.format( currentTime.getTime() ) );
        
        final Button openButton = (Button) findViewById(R.id.openButton);
        
        final Intent i = getIntent();
        
        final Station station = (Station) i.getExtras().get( Station.STATION );
        
        employeeView.setText( station.getName() );
        titleView.setText( station.getType().toString() );
        
        if ( station.getName().length() == 0 ) {
            openButton.setEnabled(  false  );
        }
        
        final Carriers selectedCarrier = preferences().getSelectedCarrier();
        
        openButton.setText( "Anfrage über " + selectedCarrier.getName() + " senden" );
        
        openButton.setOnClickListener( new View.OnClickListener() {
        
            public void onClick( @SuppressWarnings("unused") final View v ) {
                final SmsManager sms = SmsManager.getDefault();
                
                final String targetTimeValue = String.valueOf( targetTime.getText() );
                
                final String request =  station.getName() + "!" + targetText.getText() + "!" + targetTimeValue.replace(":", "") + "!";
                
                final String number = selectedCarrier.getNumber();
                
                sms.sendTextMessage( number, null, request, null, null);
                
                openButton.setEnabled( false );
                
                feedback.setText( "Anfrage \"" + request + "\" an " + number + " gesendet");
                
                preferences().setDefaultTarget( targetText.getText() );
            }
        } );
        
        
        
        
        
    }

    
    
    
}
