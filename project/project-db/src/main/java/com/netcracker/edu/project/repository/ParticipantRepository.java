package com.netcracker.edu.project.repository;

import com.netcracker.edu.project.user.project.Participation;

public interface ParticipantRepository {

    void addParticipant(String projectName, String username, Participation participation);

    void deleteParticipant(String projectName, String username);

    void updateParticipant(String projectName, String username, Participation participation);
}
