import com.netcracker.edu.auth.database.DatabaseSchema;
import com.netcracker.edu.auth.server.Server;
import com.netcracker.edu.auth.service.AuthServiceImpl;
import com.netcracker.edu.auth.session.SessionManager;
import com.netcracker.edu.auth.repository.UserRepositoryImpl;

public class MainClass {
    public static void main(String[] args) throws Exception {
        org.h2.tools.Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
        DatabaseSchema.deleteUsersTable();
        DatabaseSchema.createUsersTable();
        DatabaseSchema.createAdmin();

        new Server(new AuthServiceImpl(new UserRepositoryImpl()), new SessionManager()).start();
    }
}
