#include "Log.h"

Log *Log::s_log = NULL;

Log::Log() {

}

void Log::error(const string &tag, const string &errorText) {
#ifdef DEBUG
    //    printf("ERROR in %s : %s", tag.c_str(), errorText.c_str());
#endif // DEBUG
}

void Log::debug(const string &tag, const string &errorText) {
#ifdef DEBUG
    //    printf("DEBUG in %s : %s", tag.c_str(), errorText.c_str());
#endif // DEBUG
}

void Log::info(const string &tag, const string &errorText) {
#ifdef DEBUG
    //    printf("INFO in %s : %s", tag.c_str(), errorText.c_str());
#endif // DEBUG
}
