package com.mlins.utils.distance;

import android.location.Location;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;

public class AndroidLocationMock {

    public static void setup(){
        PowerMockito.mockStatic(Location.class);

        try {
            PowerMockito.doAnswer(
                    new Answer<Void>() {

                        @Override
                        public Void answer(InvocationOnMock invocation) throws Throwable {

                            double startLatitude = (Double) invocation.getArguments()[0];
                            double startLongitude = (Double) invocation.getArguments()[1];
                            double endLatitude = (Double) invocation.getArguments()[2];
                            double endLongitude = (Double) invocation.getArguments()[3];
                            float[] results = (float[]) invocation.getArguments()[4];

                            double latDistance = startLatitude - endLatitude;
                            double lonDistance = endLongitude - startLongitude;

                            double distance = latDistance*latDistance + lonDistance*lonDistance;

                            results[0] = (float) distance;

                            return null;
                        }
                    }
            ).when(Location.class, "distanceBetween", anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(float[].class));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
