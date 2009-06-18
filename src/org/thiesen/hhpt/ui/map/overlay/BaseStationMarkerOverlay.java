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
package org.thiesen.hhpt.ui.map.overlay;

import org.thiesen.hhpt.common.GeoLocationUtils;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.ui.activity.StationDetailsActivity;

import android.app.Activity;
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
    private final Activity _baseActivity;

    public BaseStationMarkerOverlay( final Activity baseActivity, final Bitmap marker, final Station station ) {
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
        _baseActivity.startActivity( intent );

        return true;
    }

    @Override
    public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
        drawMapLocation(canvas, mapView, shadow);
    }

    private void drawMapLocation(final Canvas canvas, final MapView mapView, final boolean shadow) {

        final Point screenCoords = new Point();
        mapView.getProjection().toPixels(GeoLocationUtils.pointFromStation( _station ), screenCoords);

        canvas.drawBitmap(_marker, screenCoords.x - _marker.getWidth()/2, screenCoords.y - _marker.getHeight(),null);
    }


}
