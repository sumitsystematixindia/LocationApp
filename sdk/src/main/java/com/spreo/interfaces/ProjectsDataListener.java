package com.spreo.interfaces;

import com.spreo.nav.interfaces.IProject;

import java.util.List;

public interface ProjectsDataListener {
    public void onPreProjectsDownload();

    public void onProjectsDataRecieved(List<IProject> projectsData);
}
