#include "ShortPathCalculator.h"

#define FLOOR_WHEGHT 10

ShortPathCalculator::ShortPathCalculator() {
}

ShortPathCalculator::~ShortPathCalculator() {
}


void ShortPathCalculator::findBestEnterAndExist(Location &origin,
                                                Location &dest,
                                                vector<Location> &origin_exits,
                                                vector<Location> &dest_exits,
                                                Location &selectedOriginExit,
                                                Location &selectedDestExit,
                                                double p2mOrigin,
                                                double p2mDest) {

    int vec_entrances_size = (int) origin_exits.size();
    int vec_exits_size = (int) dest_exits.size();


    double minDist = 10000000.0;

    // loop over entrances
    for (int i = 0; i < vec_entrances_size; i++) {

        double distOriginFromEnter = getDistanceByXYZInMeters(origin, origin_exits[i], p2mOrigin);
        //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "entrances[%d] :%s",i,origin_exits[i].poiId.c_str());
        //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "distOriginFromEnter :%f",distOriginFromEnter);
        // loop over exists
        for (int j = 0; j < vec_exits_size; j++) {

            double distEnterFromExit = getDistanceByLatLonInMeters(origin_exits[i], dest_exits[j]);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "exists[%d] :%s",j, dest_exits[j].poiId.c_str());
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "distEnterFromExit :%f",distEnterFromExit);
            double distExitFromDest = getDistanceByXYZInMeters(dest, dest_exits[j], p2mDest);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "distExitFromDest :%f",distExitFromDest);


            double totalDist = distOriginFromEnter + distEnterFromExit + distExitFromDest;
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "totalDist :%f",totalDist);
            if (totalDist < minDist) {
                minDist = totalDist;

                // keep current selected entrance
                selectedOriginExit.x = origin_exits[i].x;
                selectedOriginExit.y = origin_exits[i].y;
                selectedOriginExit.z = origin_exits[i].z;
                selectedOriginExit.lat = origin_exits[i].lat;
                selectedOriginExit.lon = origin_exits[i].lon;
                selectedOriginExit.poiId = origin_exits[i].poiId;
                selectedOriginExit.facilityId = origin_exits[i].facilityId;
                //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "selectedOriginExit.poiId :%s",selectedOriginExit.poiId.c_str());

                // keep current selected exit
                selectedDestExit.x = dest_exits[j].x;
                selectedDestExit.y = dest_exits[j].y;
                selectedDestExit.z = dest_exits[j].z;
                selectedDestExit.lat = dest_exits[j].lat;
                selectedDestExit.lon = dest_exits[j].lon;
                selectedDestExit.poiId = dest_exits[j].poiId;
                selectedDestExit.facilityId = dest_exits[j].facilityId;
                //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "selectedDestExit.poiId :%s",selectedDestExit.poiId.c_str());

            }


        }

    }


}


double ShortPathCalculator::getDistanceByXYZInMeters(Location &l1, Location &l2, double p2m) {

    return sqrt((l1.x - l2.x) * (l1.x - l2.x)
                + (l1.y - l2.y) * (l1.y - l2.y)
                + (l1.z * FLOOR_WHEGHT - l2.z * FLOOR_WHEGHT) *
                  (l1.z * FLOOR_WHEGHT - l2.z * FLOOR_WHEGHT)) / p2m;
}

double ShortPathCalculator::getDistanceByLatLonInMeters(Location &l1, Location &l2) {

    double lat1 = l1.lat;
    double lng1 = l1.lon;
    double lat2 = l2.lat;
    double lng2 = l2.lon;

    double earthRadius = 6371000; //meters
    double dLat = toRadians(lat2 - lat1);
    double dLng = toRadians(lng2 - lng1);
    double a = sin(dLat / 2) * sin(dLat / 2) +
               cos(toRadians(lat1)) * cos(toRadians(lat2)) *
               sin(dLng / 2) * sin(dLng / 2);
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));
    float dist = (float) (earthRadius * c);

    return dist;

}

double ShortPathCalculator::toRadians(double degrees) {
    double radians = degrees * M_PI / 180;
    return radians;
}




