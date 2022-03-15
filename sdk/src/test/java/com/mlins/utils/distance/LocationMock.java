package com.mlins.utils.distance;

import android.graphics.PointF;

import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import tests.PointMock;

import static org.mockito.Matchers.any;

public class LocationMock {

    public static void setup() {

        PowerMockito.spy(Location.class);

        try {
            PowerMockito.doAnswer(
                    new Answer<PointF>() {

                                @Override
                                public PointF answer(InvocationOnMock invocation) throws Throwable {
                                    Location loc = (Location) invocation.getArguments()[0];
                                    return new PointMock(loc.getX(), loc.getY());
                                }
                    }
            ).when(Location.class, "getPoint", any(ILocation.class));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
