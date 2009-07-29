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



import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.thiesen.hhpt.beans.Carriers;
import org.thiesen.hhpt.shared.model.position.Position;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.ui.activity.main.MainActivity;
import org.thiesen.hhpt.ui.common.IntentExtras;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StationDetailsActivity extends CustomActivity {

    
    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.station_details);  
        
        final Intent i = getIntent();
        final Station station = (Station) i.getExtras().get( Station.STATION );
        final Position position = (Position) i.getExtras().get( IntentExtras.CURRENT_LOCATION );
        
        Log.i( MainActivity.TAG, "Got position " + position );
        
        initTargetTextFromConfig();
        
        setTargetTimeInputValue();
        
        setDisplayValues( station );
        
        initOpenButton( station );
        initRadarButton( station );
        
        initMapsButton( position, station );
    }

    private void initTargetTextFromConfig() {
        final TextView targetText = (TextView) findViewById(R.id.edittext);
        targetText.setText( preferences().getDefaultTarget() );
    }

    private void setDisplayValues( final Station station ) {
        final TextView titleView = (TextView) findViewById(R.id.title);
        final TextView employeeView = (TextView) findViewById(R.id.employee);
        employeeView.setText( station.getName() );
        titleView.setText( station.getType().toString() );
    }

    private void setTargetTimeInputValue( ) {
        final TextView targetTime = (TextView) findViewById(R.id.edittime);
        
        final Calendar currentTime = Calendar.getInstance();
        currentTime.add( Calendar.MINUTE, 5 );
        
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        
        targetTime.setText( format.format( currentTime.getTime() ) );
    }

    private void initMapsButton( final Position position, final Station station ) {
        final Button mapsButton = (Button) findViewById(R.id.mapsButton);
        
        if ( position != null ) {
            final String sourcePart = position.getLatitude() + "," + position.getLongitude();
            final String destinationPart = station.getPosition().getLatitude() + "," + station.getPosition().getLongitude();
            
            final String url = "http://maps.google.com/maps?saddr=" + sourcePart + "&daddr=" +  destinationPart + "&dirflg=w";
            
  
            mapsButton.setOnClickListener( new View.OnClickListener() {
            
                public void onClick( @SuppressWarnings( "unused" ) final View v ) {
                    startActivity(new Intent(Intent.ACTION_VIEW,  
                            Uri.parse(url) ));
                }
            } );
            
        } else {
            mapsButton.setEnabled( false );
        }
        
    }

    private void initOpenButton( final Station station ) {
        final Button openButton = (Button) findViewById(R.id.openButton);
        
        
        if ( station.getName().length() == 0 ) {
            openButton.setEnabled(  false  );
        }
        
        final Carriers selectedCarrier = preferences().getSelectedCarrier();
        
        openButton.setText( "Anfrage über " + selectedCarrier.getName() + " senden" );
        
        openButton.setOnClickListener( new View.OnClickListener() {
        
            public void onClick( @SuppressWarnings("unused") final View v ) {
                final TextView targetText = (TextView) findViewById(R.id.edittext);
                final TextView feedback = (TextView) findViewById(R.id.feedback);
                final TextView targetTime = (TextView) findViewById(R.id.edittime);

                
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

    private void initRadarButton( final Station station ) {
        final Button radarButton = (Button) findViewById(R.id.radarButton);
        final String radarIntent = "com.google.android.radar.SHOW_RADAR";
       
        if ( isIntentAvailable( getApplicationContext(), radarIntent ) ) {
        
            radarButton.setOnClickListener( new View.OnClickListener() {

                public void onClick( @SuppressWarnings("unused") final View v ) {
                    final Intent i = new Intent( radarIntent );
                    i.putExtra("latitude", station.getPosition().getLatitude().floatValue() );
                    i.putExtra("longitude", station.getPosition().getLongitude().floatValue() );
                    startActivity(i); 
                }
            } );

        } else {
            radarButton.setEnabled( false );
        }
        
        
    }

    
    
    
}
