import com.mycompany.schoolmanagementssytem_edp.DatabaseBootstrap;
import com.mycompany.schoolmanagementssytem_edp.Role;
import com.mycompany.schoolmanagementssytem_edp.SchoolRepository;
import com.mycompany.schoolmanagementssytem_edp.User;

public class DbProbe {
    public static void main(String[] args) throws Exception {
        DatabaseBootstrap.ensureInitialized();
        SchoolRepository repository = new SchoolRepository();
        test(repository, Role.STUDENT, "2024-0001", "student123");
        test(repository, Role.STUDENT, "john.santos", "student123");
        test(repository, Role.PROFESSOR, "prof.bautista", "prof123");
        test(repository, Role.STAFF, "registrar.santos", "staff123");
        test(repository, Role.ADMIN, "admin.bulsu", "admin123");
    }

    private static void test(SchoolRepository repository, Role role, String username, String password) throws Exception {
        User user = repository.authenticate(role, username, password);
        System.out.println(role + " | " + username + " => " + (user == null ? "FAIL" : "OK:" + user.getDisplayName()));
    }
}
