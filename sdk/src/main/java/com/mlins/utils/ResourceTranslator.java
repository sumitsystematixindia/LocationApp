package com.mlins.utils;

import android.content.Context;

import java.util.HashMap;

public class ResourceTranslator {

    private static ResourceTranslator instance = null;
    private Context context = null;
    private HashMap<String, String> languagesuffixes = new HashMap<String, String>();


    public ResourceTranslator() {
        context = PropertyHolder.getInstance().getMlinsContext();
        createLanguagePrefixes();
    }

    //doesn't depend on project data, no need to cleanup
    public static ResourceTranslator getInstance() {
        if (instance == null) {
            instance = new ResourceTranslator();
        }
        return instance;
    }

    private void createLanguagePrefixes() {
        languagesuffixes.put("english", "");
        languagesuffixes.put("spanish", "_es");
        languagesuffixes.put("hebrew", "_he");
        languagesuffixes.put("arabic", "_ar");
        languagesuffixes.put("russian", "_ru");
    }

    public int getTranslatedResourceId(String type, String name) {
        String packagename = context.getPackageName();
        int result = context.getResources().getIdentifier(name, type, packagename);
        String translatedname = getTranslatedName(name);
        int tmpid = context.getResources().getIdentifier(translatedname, type, packagename);
        if (tmpid != 0) {
            result = tmpid;
        }
        return result;
    }

//	public int getTranslatedResourceId(String id) {
//		int result = Rutils.getResourseIdByName(id); 
//		String translatedid = getTranslatedName(id);
//		int tmpid = Rutils.getResourseIdByName(translatedid); 
//		if (tmpid != 0) {
//			result = tmpid;
//		}
//		return result;
//	}

    private String getTranslatedName(String name) {
        String result = name;
        String currentlanguage = PropertyHolder.getInstance().getAppLanguage();
        String suffix = languagesuffixes.get(currentlanguage);
        if (suffix != null && !suffix.isEmpty()) {
            result = name + suffix;
        }
        return result;
    }

}
