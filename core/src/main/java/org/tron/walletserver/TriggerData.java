package org.tron.walletserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;


@Data
public class TriggerData {
    private String method;//Example：transfer(unit256,address)
    private Map<String, String> parameterMap;

    public String getMethod() {
        return method;
    }

    /**
     * input: transfer(unit256,address)
     *
     * @return transfer
     */
    public String getMethodNoParams() {
        if (AddressUtil.isEmpty(method)) return "";
        String methodSub = method.contains("(") ? method.substring(0, method.indexOf("(")) : method;
        return methodSub;
    }

    /**
     * input: transfer(unit256,address)
     *
     * @return [unit256, address]
     */
    private List<String> getMethodParamsList() {
        List<String> paramsList = new ArrayList<>();
        if (AddressUtil.isEmpty(method)) return paramsList;

        int open = method.indexOf("(");
        int close = method.indexOf(")");
        // No parentheses or malformed order (e.g. ")(" ) -> no params, and avoids
        // a StringIndexOutOfBounds from substring(begin > end).
        if (open < 0 || close < open + 1) return paramsList;

        String methodSub = method.substring(open + 1, close);
        // No-arg method f(): the inner content is empty. Return an empty list
        // rather than [""] (which split(",") would otherwise yield) so the
        // parameter-count check in parseDataForTypeValueList stays correct.
        if (methodSub.trim().isEmpty() || methodSub.contains("(") || methodSub.contains(")"))
            return paramsList;

        for (String param : methodSub.split(",")) {
            paramsList.add(param.trim());
        }
        return paramsList;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return Map<" 0 ", " Tdls...de "> from parseFun
     */
    public Map<String, String> getParameterMap() {
        return parameterMap == null ? new HashMap<>() : parameterMap;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    /**
     * @return Returns metadata used for transaction parsing
     */
    public List<TypeValue> parseDataForTypeValueList() {

        List<TypeValue> typeValueList = new ArrayList<>();

        List<String> methodParamsList = getMethodParamsList();
        Map<String, String> parameterMapOld = getParameterMap();

        if (methodParamsList == null || parameterMapOld == null || methodParamsList.size() != parameterMapOld.size())
            return typeValueList;

        for (int i = 0; i < methodParamsList.size(); i++) {
            TypeValue typeValue = new TypeValue();
            String type = String.valueOf(methodParamsList.get(i));
            // accepted: Q-07 the literal "null" cannot actually occur here. The size guard
            // above only lets us in when keys are the contiguous "0".."N-1" (any skipped
            // param shrinks the map and trips the size check), and TriggerLoad.parseDataBytes
            // never returns null (it returns a non-null string or throws, and the throw paths
            // store a non-null hex string). Defensive-only finding; left unchanged.
            String value = String.valueOf(parameterMapOld.get(String.valueOf(i)));
            typeValue.setType(type);
            typeValue.setValue(value);
            typeValueList.add(typeValue);
        }
        return typeValueList;
    }

    @Data
    public static class TypeValue {
        String type;
        String value;
    }
}
