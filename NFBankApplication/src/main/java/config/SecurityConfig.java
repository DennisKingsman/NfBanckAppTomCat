package config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SecurityConfig {
    public static final String ROLE_USER = "USER";
    private static final Map<String, List<String>> mapConfig = new HashMap();

    public SecurityConfig() {
    }

    private static void init() {
        List<String> urlPatternsForUser = new ArrayList();
        urlPatternsForUser.add("/userInfo");
        urlPatternsForUser.add("/userProfile");
        urlPatternsForUser.add("/editUserProfile");
        urlPatternsForUser.add("/currentBills");
        mapConfig.put("USER", urlPatternsForUser);
    }

    public static Set<String> getAllAppRoles() {
        return mapConfig.keySet();
    }

    public static List<String> getUrlPatternsForRole(String role) {
        return (List)mapConfig.get(role);
    }

    static {
        init();
    }
}
