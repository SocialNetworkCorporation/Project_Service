package faang.school.projectservice.service;

import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.validation.ProjectValidator;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final ProjectValidator validator;

    public ProjectDto createProject(ProjectDto projectDto) {
        validator.validateForCreate(projectDto);

        Project project = mapper.toEntity(projectDto);

        project.setId(null);
        project.setStatus(ProjectStatus.CREATED);
        if (project.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }

        Project saved = projectRepository.save(project);
        log.info("Project saved with id={}", saved.getId());

        return mapper.toDto(saved);
    }
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        validator.validateForUpdate(id, projectDto);

        Project project = findProjectOrThrow(id);

        mapper.updateEntityFromDto(projectDto, project);

        Project updated = projectRepository.save(project);
        log.info("Project updated with id={}", updated.getId());

        return mapper.toDto(updated);
    }

    public List<ProjectDto> getProjects(String name, ProjectStatus status, Long requesterId) {
        return projectRepository.findAll().stream()
                .filter(project -> name == null || name.equalsIgnoreCase(project.getName()))
                .filter(project -> status == null || status.equals(project.getStatus()))
                .filter(project -> isVisibleTo(project, requesterId))
                .map(mapper::toDto)
                .toList();
    }

    public List<ProjectDto> getAllProjects(Long requesterId) {
        return projectRepository.findAll().stream()
                .filter(project -> isVisibleTo(project, requesterId))
                .map(mapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(Long id) {
        validator.validateId(id);
        Project project = findProjectOrThrow(id);

        return mapper.toDto(project);
    }

    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " not found"));
    }

    private boolean isVisibleTo(Project project, Long requesterId) {
        if (project.getVisibility() == ProjectVisibility.PUBLIC) {
            return true;
        }

        return requesterId != null && requesterId.equals(project.getOwnerId());
    }
}
