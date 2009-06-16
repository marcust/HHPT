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

package org.thiesen.hhpt.beans;


public enum Carriers {

 
    VODAFONE( "Vodafone", "+491738829999" ),
    TMOBILE( "T-Mobile", "+491753609999" ),
    O2( "O2", "+491783609999" ),
    EPLUS( "E-Plus", "+1783609999" );
            
    private final String _number;
    private final String _name;
    
    private Carriers( final String name, final String number ) {
        _name = name;
        _number = number;
    }
    
    public String getNumber() {
        return _number;
    }

    public String getName() {
        return _name;
    }
    
}
