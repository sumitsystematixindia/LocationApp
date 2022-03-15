#ifndef SwitchFloorHolder__H
#define SwitchFloorHolder__H

class SwitchFloorHolder {
private
    List <SwitchFloorObj> SwichFloorPoints = new ArrayList<SwitchFloorObj>();

    static SwitchFloorHolder instance = null;

    static
public

    SwitchFloorHolder getInstance() {
        if (instance == null) {
            instance = new SwitchFloorHolder();
        }
        return instance;
    }

public

    static void releaseInstance() {
        if (instance != null) {
            instance.clean();
            instance = null;
        }
    }

private

    void clean() {
        SwichFloorPoints.clear();
    }

public

    void load() {

        SwichFloorPoints.clear();
        try {
            File dir = PropertyHolder.getInstance().getFacilityDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File sfile = new File(dir, "switchfloor.txt");
            if (!sfile.exists()) {
                return;
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(sfile));
                String line = null;
                while ((line = in.readLine()) != null) {
                    SwitchFloorObj s = new SwitchFloorObj();
                    s.parse(line);
                    SwichFloorPoints.add(s);
                }
            } catch (IOException e) {
                e.toString();
            }
            finally {
                    if (in != null)
                    try {
                        in.close();
                    } catch (Exception e2) {
                        Log.e("", e2.getMessage());
                        e2.printStackTrace();
                    }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

public

    List <SwitchFloorObj> getSwichFloorPoints() {
        return SwichFloorPoints;
    }

public

    void setSwichFloorPoints(List <SwitchFloorObj> swichFloorPoints) {
        SwichFloorPoints = swichFloorPoints;
    }
}

#endif // SwitchFloorHolder__H
