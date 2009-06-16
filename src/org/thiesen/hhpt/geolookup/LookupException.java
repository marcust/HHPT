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

package org.thiesen.hhpt.geolookup;


public class LookupException extends Exception {

    private static final long serialVersionUID = -7450310554926450316L;

    public LookupException() {
        super();
    }

    public LookupException( final String detailMessage, final Throwable throwable ) {
        super( detailMessage, throwable );
    }

    public LookupException( final String detailMessage ) {
        super( detailMessage );
    }

    public LookupException( final Throwable throwable ) {
        super( throwable );
    }

    
    
}
