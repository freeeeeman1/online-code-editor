package com.netcracker.edu.project;

import com.netcracker.edu.auth.pool.ConnectionPool;
import com.netcracker.edu.auth.pool.ConnectionPoolImpl;
import com.netcracker.edu.logic.ProjectService;
import com.netcracker.edu.logic.ProjectServiceImpl;
import com.netcracker.edu.logic.event.EventRouter;
import com.netcracker.edu.logic.event.EventRouterImpl;
import com.netcracker.edu.logic.executor.FileFlusher;
import com.netcracker.edu.logic.executor.FileFlusherImpl;
import com.netcracker.edu.project.database.PostgresDescriptor;
import com.netcracker.edu.project.database.PostgresInitializer;
import com.netcracker.edu.project.repository.FileRepository;
import com.netcracker.edu.project.repository.FileRepositoryImpl;
import com.netcracker.edu.project.repository.ParticipantRepository;
import com.netcracker.edu.project.repository.ParticipantRepositoryImpl;
import com.netcracker.edu.project.repository.ProjectRepository;
import com.netcracker.edu.project.repository.ProjectRepositoryImpl;
import com.netcracker.edu.project.server.Server;

import java.util.concurrent.LinkedBlockingQueue;

public class ServerMain {
    public static void main(String[] args) {
        PostgresInitializer.createProjectsTable();
        PostgresInitializer.createParticipantsTable();
        PostgresInitializer.createFilesTable();

        ConnectionPool connectionPool = ConnectionPoolImpl.create(
                PostgresDescriptor.getUrl(),
                PostgresDescriptor.getUser(),
                PostgresDescriptor.getPassword(),
                PostgresDescriptor.getInitialPoolSize(),
                PostgresDescriptor.getDriver());

        ProjectRepository projectRepository = new ProjectRepositoryImpl(connectionPool);
        FileRepository fileRepository = new FileRepositoryImpl(connectionPool);
        ParticipantRepository participantRepository = new ParticipantRepositoryImpl(connectionPool);
        FileFlusher fileFlusher = new FileFlusherImpl(fileRepository, new LinkedBlockingQueue<>());
        EventRouter eventRouter = new EventRouterImpl();

        ProjectService projectService = new ProjectServiceImpl(
                projectRepository,
                fileRepository,
                participantRepository,
                fileFlusher,
                eventRouter);

        Thread serverThread = new Thread(() -> {
            Server server = new Server(projectService);
            server.start();

        });

        serverThread.start();
    }
}
