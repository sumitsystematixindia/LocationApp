package com.mlins.utils;

public class Rutils {

    public static int getResourseIdByName(String rid) {
        Class r = null;
        int id = 0;
        try {

            //System.out.println("Rutils -> "+ rid);
            String packageName = "com.spreo.androidspreolibrary";

            String ls[] = rid.split("\\.");
            String className = ls[1];
            String name = ls[2];

            r = Class.forName(packageName + ".R");

            Class[] classes = r.getClasses();
            Class desireClass = null;

            for (int i = 0; i < classes.length; i++) {
                if (classes[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = classes[i];

                    break;
                }
            }

            if (desireClass != null) {
                id = desireClass.getField(name).getInt(desireClass);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return id;
    }
}
