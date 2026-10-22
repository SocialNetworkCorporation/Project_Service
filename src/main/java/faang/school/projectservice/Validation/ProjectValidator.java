package faang.school.projectservice.Validation;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.exception.ProjectValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private static final int NAME_MAX_LENGTH = 128;
    private static final int DESCRIPTION_MAX_LENGTH = 4096;

    private final ProjectRepository projectRepository;

    public void validateForCreate(ProjectDto dto) {
        if (dto == null) {
            throw new ProjectValidationException("ProjectDto is null");
        }

        validateName(dto.getName());
        validateDescription(dto.getDescription());

        if (dto.getOwnerId() == null) {
            throw new ProjectValidationException("OwnerId is null");
        }

        if (projectRepository.existsByOwnerIdAndName(dto.getOwnerId(), dto.getName())) {
            throw new ProjectValidationException("Project with name " + dto.getName() + " already exists for this owner");
        }
    }
    public void validateForUpdate(Long id, ProjectDto dto) {
        validateId(id);

        if (dto == null) {
            throw new ProjectValidationException("ProjectDto must not be null");
        }

        if (dto.getName() != null) {
            validateName(dto.getName());
        }

        if (dto.getDescription() != null) {
            validateDescription(dto.getDescription());
        }
    }
    public void validateId(Long id) {
        if (id == null) {
            throw new ProjectValidationException("Project Id must not be null");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ProjectValidationException("Project name must not be null");
        }

        if (name.length() > NAME_MAX_LENGTH) {
            throw new ProjectValidationException("Project name must not be longer than " + NAME_MAX_LENGTH + " characters");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new ProjectValidationException("Description must be at most " + DESCRIPTION_MAX_LENGTH + " characters");
        }
    }
}
