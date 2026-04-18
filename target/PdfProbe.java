import java.nio.file.Path;
import com.mycompany.schoolmanagementssytem_edp.DatabaseBootstrap;
import com.mycompany.schoolmanagementssytem_edp.Role;
import com.mycompany.schoolmanagementssytem_edp.SchoolRepository;
import com.mycompany.schoolmanagementssytem_edp.User;

public class PdfProbe {
    public static void main(String[] args) throws Exception {
        DatabaseBootstrap.ensureInitialized();
        SchoolRepository repository = new SchoolRepository();
        User user = repository.authenticate(Role.STUDENT, "2024-0001", "student123");
        repository.exportStudentGradesPdf(user, Path.of("target", "sample-rog.pdf"));
        repository.exportStudentCorPdf(user, Path.of("target", "sample-cor.pdf"));
        System.out.println("OK");
    }
}
