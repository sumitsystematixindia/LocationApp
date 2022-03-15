package com.mlins.locator;

import com.mlins.utils.Lookup;

import java.util.List;

public class BaseMatrixDataHelper {

    @SuppressWarnings({"static-access", "static-access"})
    public static BaseMatrixDataHelper getInstance() {
        BaseMatrixDataHelper instance = Lookup.getInstance().lookup(BaseMatrixDataHelper.class);
        if (instance == null) {
            BaseMatrixDataHelper inst = new BaseMatrixDataHelper();
            try {
                instance = (BaseMatrixDataHelper) (inst.getClass().forName("com.com.com.mlins.utils.MatrixDataHelper").newInstance());
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Lookup.getInstance().put(instance, BaseMatrixDataHelper.class);
        }

        return instance;
    }

    public static void setInstance(BaseMatrixDataHelper helper) {
        Lookup.getInstance().put(helper, BaseMatrixDataHelper.class);
    }

    public void setMatrix(List<AssociativeData> theList) {
        // TODO Auto-generated method stub

    }

    public void setSSIDNames(List<String> ssidnames) {
        // TODO Auto-generated method stub

    }


}
