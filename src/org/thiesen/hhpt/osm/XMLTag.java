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

package org.thiesen.hhpt.osm;


public class XMLTag {
    
    private final String _key;
    private final String _value;
    
    private XMLTag( final String key, final String value ) {
        _key = key;
        _value = value;
    }

    public static XMLTag valueOf( final String key, final String value ) {
        return new XMLTag( key, value );
        
    }

    public String getValue() {
        return _value;
        
    }

    public String getKey() {
        return _key;
        
    }

    public boolean keyIs( final String key ) {
        return _key.equalsIgnoreCase( key );
        
    }

    public boolean keyAnyOf( final Iterable<String> keys ) {
        for ( final String key : keys ) {
            if ( _key.equalsIgnoreCase( key ) ) {
                return true;
            }
        }
        return false;
    }

}
