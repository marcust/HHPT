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
package org.thiesen.hhpt.ui.activity.main;


public class LookupPosition {
    
    private final double _latitude;
    private final double _longitude;
  
    public LookupPosition( final double latitude, final double longitude ) {
        super();
        _latitude = latitude;
        _longitude = longitude;
    }
    
    public double getLatitude() {
        return _latitude;
    }
    public double getLongitude() {
        return _longitude;
    }
    
    
    
}
