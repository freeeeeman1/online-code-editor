package com.netcracker.edu.project.repository;

import com.netcracker.edu.project.user.project.File;

public interface FileRepository {

    void addFile(File file, String projectName);

    void deleteFile(String path);

    void updateContentFile(String path, String content);
}
