#ifndef ML_LOG__H
#define ML_LOG__H

#include <string>

using namespace std;

class Log {
    static Log *s_log;

    Log();

public:
    static Log &getInstance() {
        if (s_log == NULL) {
            s_log = new Log();
        }
        return *s_log;
    }

    void error(const string &tag, const string &errorText);

    void debug(const string &tag, const string &errorText);

    void info(const string &tag, const string &errorText);
};

#endif // ML_LOG__H