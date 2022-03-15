#include "Location.h"
#include "PoiData.h"

Location::Location() :
        x(0), y(0), z(0) {
}


Location::~Location() {
}


Location::Location(double x, double y, double z, double lat, double lon, string poiId,
                   string facilityId) :
        x(x), y(y), z(z), lat(lat), lon(lon), poiId(poiId), facilityId(facilityId) {
}

Location::Location(double x, double y, double z, double lat, double lon) :
        x(x), y(y), z(z), lat(lat), lon(lon) {
}

Location::Location(const PoiData &data) :
        x(data.getPoint().getX()), y(data.getPoint().getY()), z(data.getZ()) {
    description = data.getpoiDescription();
    iconUri = data.getPoiuri();

    keywordslist = data.getPoiKeywords();
}

double Location::proximity(Location other) {

    double xdiff = x - other.x;
    double ydiff = y - other.y;
    double dist = sqrt(xdiff * xdiff + ydiff * ydiff);

    if (type == 1 && other.type == 1) {
        dist = findLatLonDistance(lat, lon, other.lat, other.lon);

//		__android_log_print(ANDROID_LOG_ERROR, "distance --> Location::proximity",
//											"dist: %f --> (x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s) , "
//											"(x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//											dist, x, y, z,
//											lat, lon,
//											type,facilityId.c_str(),
//											other. x, other.y, other.z,
//											other.lat, other.lon,
//											other.type, other.facilityId.c_str());

//		__android_log_print(ANDROID_LOG_ERROR, "distance --> Location::proximity",
//												"dist: %f --> (lat:%f , lon:%f, type: %d, facid: %s) , "
//												"(lat:%f , lon:%f, type: %d, facid: %s)",
//												dist,
//												lat, lon,
//												type,facilityId.c_str(),
//												other.lat, other.lon,
//												other.type, other.facilityId.c_str());
    }

    return dist;
}

void Location::initProximity(Location other) {


    double xdiff = x - other.x;
    double ydiff = y - other.y;
    setdistance_to_origin(sqrt(xdiff * xdiff + ydiff * ydiff));

    if (type == 1 && other.type == 1) {
        double dist = findLatLonDistance(lat, lon, other.lat, other.lon);
        setdistance_to_origin(dist);
//		__android_log_print(ANDROID_LOG_ERROR, "distance --> Location::initProximity",
//													"dist: %f --> (x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s) , "
//													"(x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//													dist, x, y, z,
//													lat, lon,
//													type,facilityId.c_str(),
//													other. x, other.y, other.z,
//													other.lat, other.lon,
//													other.type, other.facilityId.c_str());


//		__android_log_print(ANDROID_LOG_ERROR, "distance --> Location::initProximity",
//														"dist: %f --> (lat:%f , lon:%f, type: %d, facid: %s) , "
//														"(lat:%f , lon:%f, type: %d, facid: %s)",
//														dist,
//														lat, lon,
//														type,facilityId.c_str(),
//
//														other.lat, other.lon,
//														other.type, other.facilityId.c_str());
    }
}

//void Location::proximity(double x, double y) {
//	double cx = this->x;
//	double cy = this->y;
//	if (type == 1) {
//		cx = this->lat;
//		cy = this->lon;
//	}
//	double xdiff = cx - x;
//	double ydiff = cy - y;
//	double dist = sqrt(xdiff * xdiff + ydiff * ydiff);
//	setdistance_to_origin(dist);
//
//}

double Location::proximity() {
    return getdistance_to_origin();
}

double Location::findLatLonDistance(double lat1, double lon1, double lat2, double lon2) {

    double R = 6371000; // metres
    double phi1 = toRadians(lat1);
    double phi2 = toRadians(lat2);
    double delta_phi = toRadians(lat2 - lat1);
    double delta_lamda = toRadians(lon2 - lon1);

    double a = sin(delta_phi / 2) * sin(delta_phi / 2) +
               cos(phi1) * cos(phi2) * sin(delta_lamda / 2) * sin(delta_lamda / 2);
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));

    double d = R * c;
    return d;


}

double Location::toRadians(double degrees) {
    double radians = degrees * M_PI / 180;
    return radians;
}

