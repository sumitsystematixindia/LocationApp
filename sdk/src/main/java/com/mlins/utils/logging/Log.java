package com.mlins.utils.logging;

import java.io.File;
import java.util.ArrayList;

public final class Log {
    private static Log instance = null;

    public static boolean debugMode = false;

    public Boolean loadedonce = false;
    public Boolean loadedfilters = false;
    boolean writeit = false;
    int logginglevel = 0;//PropertyHolder.getInstance().getLogginglevel();
    private ArrayList<File> logfiles = new ArrayList<File>();
    private String logdir = "";
    private String compdir = "";

    private Log() {
//		loadonce();
    }

    //does nothing for now, no need to rework
    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    public static void d(String tag, String message) {
        d(tag, message, null);
    }

    public static void d(String tag, String message, Throwable t) {
        if(debugMode)
            android.util.Log.d(tag, message, t);
    }

    public static void i(String tag, String message) {
        i(tag, message, null);
    }

    public static void i(String tag, String message, Throwable t) {
        if(debugMode)
            android.util.Log.i(tag, message, t);
    }

    public static void e(String tag, String message) {
        e(tag, message, null);
    }

    public static void e(String tag, String message, Throwable t) {
        if(debugMode)
            android.util.Log.i(tag, message, t);
    }

    public void loadonce() {
//		if (!loadedonce) {
//			LogFilter lf2 = new LogFilter();
//			lf2.loadfilterfile();
//			logdir = PropertyHolder.getInstance().getAppDir() + File.separator
//					+ "logs";
//			compdir = PropertyHolder.getInstance().getAppDir() + File.separator
//					+ "oldlogs";
//			File f = new File(logdir);
//			if (!f.exists())
//				f.mkdirs();
//			File compf = new File(compdir);
//			if (!compf.exists())
//				compf.mkdirs();
//			logfiles = listoflogs(logdir);
//			if (logfiles.size() > 1) {
//				zipFileAtPath(logdir, compdir);
//			}
//			logginglevel = PropertyHolder.getInstance().getLogginglevel();
//			setLoadonce(true);
//		}
    }

//	public ArrayList<File> listoflogs(String directoryName) {
//		File directory = new File(directoryName);
//		File[] fList = directory.listFiles();
//		ArrayList<File> files = new ArrayList<File>();
//		for (File file : fList) {
//			if (file.isFile()) {
//				files.add(file);
//			} else if (file.isDirectory()) {
//				listoflogs(file.getAbsolutePath());
//			}
//		}
//		return files;
//	}

//	public void appendLog(String text) {
//		LogFilter lf = new LogFilter();
//		writeit = lf.filterpackage(text);
//		if (writeit) {
//			String logdate = "yyMMdd";
//			String date_time = "yyMMdd HH:mm:ss.SSSZ";
//			SimpleDateFormat sdf = new SimpleDateFormat(logdate);
//			SimpleDateFormat sdf1 = new SimpleDateFormat(date_time);
//			File logFile = new File(PropertyHolder.getInstance().getAppDir()
//					+ File.separator + "logs" + File.separator + "log_"
//					+ sdf.format(new Date()) + ".txt");
//			if (!logFile.exists()) {
//				try {
//					logFile.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			try {
//				BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
//						true));
//				buf.append(sdf1.format(new Date()) + " " + text);
//				buf.newLine();
//				buf.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

    public void debug(String tag, String message) {
//		if (logginglevel > 3) {
//			appendLog(tag + " " + message);
//		}
    }

    public void debug(String tag, String message, Throwable t) {
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		t.printStackTrace(pw);
//		String stack = sw.toString();
//		appendLog(tag + " " + message + " " + stack);
    }

    public void info(String tag, String message) {
//		if (logginglevel > 2) {
//			appendLog(tag + " " + message);
//		}
    }

    public void info(String tag, String message, Throwable t) {
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		t.printStackTrace(pw);
//		String stack = sw.toString();
//		appendLog(tag + " " + message + " " + stack);
    }

    public void error(String tag, String message) {
//		if (logginglevel > 1) {
//			appendLog(tag + " " + message);
//		}
    }

    public void error(String tag, String message, Throwable t) {
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		t.printStackTrace(pw);
//		String stack = sw.toString();
//		appendLog(tag + " " + message + " " + stack);
    }

    public void fatal(String tag, String message) {
//		if (logginglevel > 0) {
//			appendLog(tag + " " + message);
//		}
    }

    public void fatal(String tag, String message, Throwable t) {
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		t.printStackTrace(pw);
//		String stack = sw.toString();
//		appendLog(tag + " " + message + " " + stack);
    }

	

	/*
     *
	 * Zips a file at a location and places the resulting zip file at the
	 * toLocation Example: zipFileAtPath("downloads/myfolder",
	 * "downloads/myFolder.zip");
	 */

//	public boolean zipFileAtPath(String sourcePath, String toLocation) {
//		// ArrayList<String> contentList = new ArrayList<String>();
//		final int BUFFER = 2048;
//
//		File sourceFile = new File(sourcePath);
//		try {
//			BufferedInputStream origin = null;
//			File destFile = new File(toLocation);
//			File des = null;
//			if(destFile.isDirectory()){
//				des = new File(destFile,"logs" + ".zip");
//			}
//			else{
//				des = destFile;
//			}
//			
//			BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(des));
//			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
//			if (sourceFile.isDirectory()) {
//				zipSubFolder(out, sourceFile, sourceFile.getParent().length());
//			} else {
//				byte data[] = new byte[BUFFER];
//				FileInputStream fi = new FileInputStream(sourcePath);
//				origin = new BufferedInputStream(fi, BUFFER);
//				ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
//				out.putNextEntry(entry);
//				int count;
//				while ((count = origin.read(data, 0, BUFFER)) != -1) {
//					out.write(data, 0, count);
//				}
//			}
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//
//	/*
//	 * 
//	 * Zips a subfolder
//	 */
//
//	private void zipSubFolder(ZipOutputStream out, File folder,
//			int basePathLength) throws IOException {
//
//		final int BUFFER = 2048;
//
//		File[] fileList = folder.listFiles();
//		BufferedInputStream origin = null;
//		for (File file : fileList) {
//			if (file.isDirectory()) {
//				zipSubFolder(out, file, basePathLength);
//			} else {
//				byte data[] = new byte[BUFFER];
//				String unmodifiedFilePath = file.getPath();
//				String relativePath = unmodifiedFilePath
//						.substring(basePathLength);
//				FileInputStream fi = new FileInputStream(unmodifiedFilePath);
//				origin = new BufferedInputStream(fi, BUFFER);
//				ZipEntry entry = new ZipEntry(relativePath);
//				out.putNextEntry(entry);
//				int count;
//				while ((count = origin.read(data, 0, BUFFER)) != -1) {
//					out.write(data, 0, count);
//				}
//				origin.close();
//			}
//		}
//	}
//
//	/*
//	 * gets the last path component
//	 * 
//	 * Example: getLastPathComponent("downloads/example/fileToZip"); Result:
//	 * "fileToZip"
//	 */
//	public String getLastPathComponent(String filePath) {
//		String[] segments = filePath.split("/");
//		String lastPathComponent = segments[segments.length - 1];
//		return lastPathComponent;
//	}

    public Boolean getLoadonce() {
        return loadedonce;
    }

    public void setLoadonce(Boolean loadonce) {
        this.loadedonce = loadonce;
    }

    public Boolean getLoadedfilters() {
        return loadedfilters;
    }

    public void setLoadedfilters(Boolean loadedfilters) {
        this.loadedfilters = loadedfilters;
    }


}
