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
        
            public void onClick( final View v ) {
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
