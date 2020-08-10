
package request;

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class UserRoleRequestWrapper extends HttpServletRequestWrapper {
    private String user;
    private List<String> roles;
    private HttpServletRequest request;

    public UserRoleRequestWrapper(String user, List<String> roles, HttpServletRequest request) {
        super(request);
        this.user = user;
        this.request = request;
        this.roles = roles;
    }

    public boolean isUserInRole(String role) {
        return this.roles == null ? this.request.isUserInRole(role) : this.roles.contains(role);
    }

    public Principal getUserPrincipal() {
        return this.user == null ? this.request.getUserPrincipal() : new Principal() {
            public String getName() {
                return UserRoleRequestWrapper.this.user;
            }
        };
    }
}