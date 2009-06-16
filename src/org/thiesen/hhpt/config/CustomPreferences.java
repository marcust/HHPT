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
