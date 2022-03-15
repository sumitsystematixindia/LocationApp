package com.mlins.nav;

public class MultiNavUtils {

    /**
     public static List<ILocation> getOrder(ILocation origin, List<ILocation> dests) {
     List<ILocation> OrderdDests = null;
     String jsonFormat = convertToJson(origin, dests);
     MultiNavDestsOrderTask task = new MultiNavDestsOrderTask();
     try {
     String result = task.execute(jsonFormat).get(10000, TimeUnit.MILLISECONDS);
     OrderdDests = parseJson(result);
     } catch (Throwable e) {
     e.printStackTrace();
     }


     if (OrderdDests == null) {
     return dests;
     }
     return OrderdDests;

     }

     public static List<IPoi> getOrder(IPoi origin, List<IPoi> dests) {
     return null;
     }

     private static String convertToJson(ILocation origin, List<ILocation> dests) {

     String content = null;
     JSONObject jsonObj = null;
     if (dests == null || origin == null) {
     return null;
     }

     if (dests.size() == 0) {
     return null;
     }

     ILocation destLoc = null;

     try {
     jsonObj = new JSONObject();

     int reqCode = 1;
     jsonObj.put("req", reqCode);
     String projectId = PropertyHolder.getInstance().getProjectId();
     jsonObj.put("pid", projectId);

     String campusId = PropertyHolder.getInstance().getCampusId();
     jsonObj.put("cid", campusId);

     String facilityId = PropertyHolder.getInstance().getFacilityID();
     jsonObj.put("fid", facilityId);

     JSONObject originLocJsonObj = new JSONObject();

     originLocJsonObj.put("x", origin.getX());
     originLocJsonObj.put("y", origin.getY());
     originLocJsonObj.put("z", origin.getZ());
     jsonObj.put("origin", originLocJsonObj);

     JSONArray destJsonArray = new JSONArray();

     for (int i = 0; i < dests.size(); i++) {

     destLoc = dests.get(i);
     JSONObject destObj = new JSONObject();
     destObj.put("x", destLoc.getX());
     destObj.put("y", destLoc.getY());
     destObj.put("z", destLoc.getZ());

     destJsonArray.put(destObj);

     }

     jsonObj.put("dests", destJsonArray);

     content = jsonObj.toString(2);

     } catch (Throwable t) {
     t.printStackTrace();
     jsonObj = null;
     content = null;
     }

     return content;

     }

     private static List<ILocation> parseJson(String content) {

     List<ILocation> dests = null;

     try {
     JSONTokener jsonTokener = new JSONTokener(content);
     dests = new ArrayList<ILocation>();
     JSONObject json = (JSONObject) jsonTokener.nextValue();

     double x = -1;
     double y = -1;
     int z = -100;

     // JSONObject origin = json.getJSONObject("origin");
     // x = origin.getDouble("x");
     // y = origin.getDouble("y");
     // z = origin.getInt("z");
     //
     // Location originLoc = new Location((float) x, (float) y, z);
     //
     // dests.add(originLoc);

     JSONArray jDestsObj = json.getJSONArray("dests");

     for (int i = 0; i < jDestsObj.length(); i++) {

     JSONObject dest = jDestsObj.getJSONObject(i);
     x = dest.getDouble("x");
     y = dest.getDouble("y");
     z = dest.getInt("z");
     Location destPoiLoc = new Location((float) x, (float) y, z);
     dests.add(destPoiLoc);

     }

     } catch (Throwable t) {
     dests = null;
     t.printStackTrace();
     }

     return dests;
     }

     private static class MultiNavDestsOrderTask extends AsyncTask<String, String, String> {

     private final String SERVERADDRESS = PropertyHolder.getInstance().getServerName() + "navtopois";

     @Override protected String doInBackground(String... req) {
     String result = null;
     try {
     String content = req[0];
     URL obj = new URL(SERVERADDRESS);
     HttpURLConnection con = (HttpURLConnection) obj.openConnection();

     // add reuqest header
     con.setRequestMethod("POST");

     con.setRequestProperty("charset", "utf-8");

     con.setRequestProperty("Content-Length", "" + Integer.toString(content.length()));

     // Send post request
     con.setDoOutput(true);
     DataOutputStream wr = new DataOutputStream(con.getOutputStream());
     wr.writeBytes(content);
     wr.flush();
     wr.close();

     //int responseCode = con.getResponseCode();
     //System.out.println("\nSending 'POST' request to URL : " + SERVERADDRESS);
     //System.out.println("Post parameters : " + content);
     //System.out.println("Response Code : " + responseCode);

     BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
     String inputLine;
     StringBuffer response = new StringBuffer();

     while ((inputLine = in.readLine()) != null) {
     response.append(inputLine);
     }
     in.close();

     result = response.toString();

     } catch (Throwable e) {
     e.printStackTrace();
     }
     return result;
     }

     }
     */

}
