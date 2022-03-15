package com.mlins.utils.logging;

import com.mlins.utils.PropertyHolder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LogFilter {
    final static private File logconfigdir = PropertyHolder.getInstance()
            .getAppDir();
    final static private String logconfigfilename = "logconfig.txt";
    final static private String logconfigfile = logconfigdir + "/"
            + logconfigfilename;
    boolean result = false;
    private ArrayList<String> packagelistfilter = new ArrayList<String>();
    private ArrayList<String> listfilter = new ArrayList<String>();

    public void loadfilterfile() {
        File file = new File(logconfigfile);
        try {
            if (!file.exists()) {
                creatconfigfile();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = in.readLine()) != null) {
                if ((line.matches(".*\\d.*")) && (line.length() < 3)) {
                    {
                        try {
                            boolean containsDigit = false;
                            for (char c : line.toCharArray()) {
                                if (containsDigit = Character.isDigit(c)) {
                                    String level = "" + c;
                                    PropertyHolder.getInstance().setLogginglevel(
                                            Integer.parseInt(level));
                                    break;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }
                if (line.contains("com")) {
                    try {
                        String pack = new String();
                        pack = line;
                        packagelistfilter.add(pack);
                    } catch (Exception e) {

                    }
                }
            }
            PropertyHolder.getInstance().setFilteredpackages(packagelistfilter);

        } catch (IOException e) {
            e.toString();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public boolean filterpackage(String packagetofilter) {
        if (!Log.getInstance().getLoadedfilters()) {
            loadfilterfile();
            Log.getInstance().setLoadedfilters(true);
        }
        boolean filter = false;
        listfilter = PropertyHolder.getInstance().getFilteredpackages();
        for (String s : listfilter)
            if (packagetofilter.contains(s)) {
                filter = true;
                break;
            }

        return filter;
    }

    private void creatconfigfile() {
        StringBuffer sb = new StringBuffer();
        sb.append("0" + "\n");
        sb.append("com" + "\n");
        sb.append("packages written above this line will be written to log" + "\n");
        sb.append("Instructions: " + "\n");
        sb.append("The fisrt digit sets the logging level as follows: " + "\n");
        sb.append(" Crashes (Exception and Throwable) are always written" + "\n");
        sb.append(" 1 = Fatal " + "\n");
        sb.append(" 2 = Error " + "\n");
        sb.append(" 3 = Info " + "\n");
        sb.append(" 4 = Debug " + "\n");
        File dir = logconfigdir;

        if (!dir.exists()) {
            dir.mkdirs();
        }
        File pfile = new File(dir, logconfigfilename);
        try {
            pfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(pfile, true));
            out.write(sb.toString());
            out.flush();
        } catch (IOException e) {
            e.toString();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
