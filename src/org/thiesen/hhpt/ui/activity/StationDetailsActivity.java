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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StationDetailsActivity extends CustomActivity {

    private final static String RADAR_INTENT = "com.google.android.radar.SHOW_RADAR";
    private static final int MENU_SHOW_DIRECTIONS = 1;
    private static final int MENU_SHOW_RADAR = 2;
    private static final int MENU_SHOW_AR = 3;
    private Station _station;
    private Position _lastPosition;
    
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
        
        _station = station;
        _lastPosition = position;
        
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

    private void showDirections( ) {
        final String sourcePart = _lastPosition.getLatitude() + "," + _lastPosition.getLongitude();
        final String destinationPart = _station.getPosition().getLatitude() + "," + _station.getPosition().getLongitude();
        
        final String url = "http://maps.google.com/maps?saddr=" + sourcePart + "&daddr=" +  destinationPart + "&dirflg=w";
                  
        
        startActivity(new Intent(Intent.ACTION_VIEW,  
                        Uri.parse(url) ));
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


    private void showRadar() {
        final Intent i = new Intent( RADAR_INTENT );
        i.putExtra("latitude", _station.getPosition().getLatitude().floatValue() );
        i.putExtra("longitude", _station.getPosition().getLongitude().floatValue() );
        startActivity(i);
    }

    private boolean isRadarAvailable() {
        return isIntentAvailable( getApplicationContext(), RADAR_INTENT );
    }

    
    
    @Override
    public boolean onCreateOptionsMenu( final Menu menu ) {
        menu.add(0, MENU_SHOW_DIRECTIONS, 0, "Show Directions" ).setIcon( android.R.drawable.ic_menu_mapmode );
        
        if ( isRadarAvailable() ) {
            menu.add(0, MENU_SHOW_RADAR, 0, "Show Radar" ).setIcon( android.R.drawable.ic_menu_compass );
        }
        menu.add(0, MENU_SHOW_AR, 0, "AR View" );

        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SHOW_DIRECTIONS:
                showDirections();
                return true;
            case MENU_SHOW_RADAR: 
                showRadar();
                return true;
            case MENU_SHOW_AR:
                showAr();
                return true;

        }
        return false;
    }

    private void showAr() {
//        final WikitudeARHelper helper = new WikitudeARHelper( getApplication(),
//                getResources().getResourceName( R.drawable.train ),
//                getResources().getResourceName( R.drawable.bus ) );
//        startActivity( helper.makeWikitudeIntent( _station ) );
    }
    
    
    
}
