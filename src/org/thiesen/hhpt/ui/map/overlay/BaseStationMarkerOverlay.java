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
package org.thiesen.hhpt.ui.map.overlay;

import org.thiesen.hhpt.common.GeoLocationUtils;
import org.thiesen.hhpt.shared.model.position.Position;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.ui.activity.StationDetailsActivity;
import org.thiesen.hhpt.ui.activity.main.MainActivity;
import org.thiesen.hhpt.ui.common.IntentExtras;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;


public class BaseStationMarkerOverlay extends Overlay {

    private final Bitmap _marker;
    private final Station _station;
    private final MainActivity _baseActivity;

    public BaseStationMarkerOverlay( final MainActivity baseActivity, final Bitmap marker, final Station station ) {
        _baseActivity = baseActivity;
        _marker = marker;
        _station = station;
    }

    @Override
    public boolean onTap(final GeoPoint p, final MapView mapView)  {
        final RectF hitTestRecr = new RectF();
        
        final Point screenCoords = new Point();
        mapView.getProjection().toPixels(GeoLocationUtils.pointFromStation( _station ), screenCoords );

        hitTestRecr.set(-_marker.getWidth()/2,-_marker.getHeight(),_marker.getWidth()/2,0);
        hitTestRecr.offset(screenCoords.x,screenCoords.y);

        mapView.getProjection().toPixels(p, screenCoords);
        
        if (!hitTestRecr.contains(screenCoords.x,screenCoords.y)) {
            return false; 
        }

        final Intent intent = new Intent( _baseActivity.getApplicationContext(), StationDetailsActivity.class );
        intent.putExtra( Station.STATION, _station );
        final Position lastPosition = _baseActivity.getLastPosition();

        intent.putExtra( IntentExtras.CURRENT_LOCATION, lastPosition );
        
        _baseActivity.startActivity( intent );
        return true;
    }

    @Override
    public void draw(final Canvas canvas, final MapView mapView, @SuppressWarnings( "unused" ) final boolean shadow) {
        drawMapLocation( canvas, mapView );
    }

    private void drawMapLocation(final Canvas canvas, final MapView mapView ) {

        final Point screenCoords = new Point();
        mapView.getProjection().toPixels(GeoLocationUtils.pointFromStation( _station ), screenCoords);

        canvas.drawBitmap(_marker, screenCoords.x - _marker.getWidth()/2, screenCoords.y - _marker.getHeight(),null);
    }


}
