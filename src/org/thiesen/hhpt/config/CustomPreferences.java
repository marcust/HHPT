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
package org.thiesen.hhpt.config;

import org.thiesen.hhpt.beans.Carriers;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CustomPreferences {

    private static final String DEFAULT_TARGET = "DEFAULT_TARGET";
    private static final String SELECTED_CARRIER = "SELECTED_CARRIER";
    private final SharedPreferences _sharedPreferences;
    
    public CustomPreferences( final SharedPreferences sharedPreferences ) {
        _sharedPreferences = sharedPreferences;
    }
    
    public Carriers getSelectedCarrier() {
        final int selected = _sharedPreferences.getInt( SELECTED_CARRIER, 0 );
        
        for ( final Carriers number : Carriers.values() ) {
            if ( number.ordinal() == selected ) return number;
        }
        
        return null;
    }

    public String getDefaultTarget() {
        
        return _sharedPreferences.getString( DEFAULT_TARGET, "Mittelweg 44" );
    }

    public void setDefaultTarget( final CharSequence text ) {
       final Editor edit = _sharedPreferences.edit();
       
       edit.putString( DEFAULT_TARGET, String.valueOf( text ) );
       
       edit.commit();
        
    }

    public void setSelectedCarrier( final Carriers carrier ) {
        
        final Editor edit = _sharedPreferences.edit();
        
        edit.putInt( SELECTED_CARRIER,  carrier.ordinal()  );
        
        edit.commit();
    }

}
