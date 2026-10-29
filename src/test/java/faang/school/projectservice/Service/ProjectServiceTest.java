package faang.school.projectservice.Service;

import faang.school.projectservice.Mapper.ProjectMapper;
import faang.school.projectservice.Mapper.ProjectMapperImpl;
import faang.school.projectservice.Validation.ProjectValidator;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

        @Test
    void createProject_success() {
        fail("CI check");
    }

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private ProjectMapperImpl projectMapper;

    @InjectMocks
    private ProjectService projectService;

    ProjectDto dto;
    Project project;

    @BeforeEach
    void setup() {

        dto = ProjectDto.builder()
                .name("Test")
                .ownerId(1L)
                .build();

        project = Project.builder()
                .name("Test")
                .ownerId(1L)
                .build();
    }

    @Nested
    class createProjectTests {

        @Test
        void createProject_success() {

            when(projectMapper.toEntity(dto)).thenReturn(project);
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            ProjectDto result = projectService.createProject(dto);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Test");

            verify(projectValidator).validateForCreate(dto);
        }

        @Test
        void setsIdNullAndStatusCreated() {

            project.setId(999L);

            when(projectMapper.toEntity(dto)).thenReturn(project);
            when(projectRepository.save(any(Project.class)))
                    .thenAnswer(i -> i.getArgument(0));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            projectService.createProject(dto);

            ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
            verify(projectRepository).save(captor.capture());
            Project savedProject = captor.getValue();
            assertThat(savedProject.getId()).isNull();
            assertThat(savedProject.getStatus()).isEqualTo(ProjectStatus.CREATED);
        }

        @Test
        void ifVisibilityIsNullSetPublic() {

            project.setVisibility(null);

            when(projectMapper.toEntity(dto)).thenReturn(project);
            when(projectRepository.save(any(Project.class)))
                    .thenAnswer(i -> i.getArgument(0));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            projectService.createProject(dto);

            ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
            verify(projectRepository).save(captor.capture());
            Project savedProject = captor.getValue();
            assertThat(savedProject.getVisibility()).isEqualTo(ProjectVisibility.PUBLIC);
        }

        @Test
        void ifVisibilityIsNotNullSetThis() {

            project.setVisibility(ProjectVisibility.PRIVATE);

            when(projectMapper.toEntity(dto)).thenReturn(project);
            when(projectRepository.save(any(Project.class)))
                    .thenAnswer(i -> i.getArgument(0));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            projectService.createProject(dto);

            ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
            verify(projectRepository).save(captor.capture());
            Project savedProject = captor.getValue();
            assertThat(savedProject.getVisibility()).isEqualTo(ProjectVisibility.PRIVATE);
        }
    }

    @Nested
    class updateProjectTests {

        @Test
        void updateProject_success() {

            Long projectId = 1L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            when(projectRepository.save(project)).thenReturn(project);
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            ProjectDto result = projectService.updateProject(projectId, dto);

            assertThat(result).isNotNull();
            verify(projectValidator).validateForUpdate(projectId, dto);
            verify(projectMapper).updateEntityFromDto(dto, project);
            verify(projectRepository).save(project);
        }

        @Test
        void updateProject_notFound_throws() {
            Long projectId = 99L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    projectService.updateProject(projectId, dto))
                    .isInstanceOf(ProjectNotFoundException.class);

            verify(projectRepository, never()).save(any(Project.class));
        }
    }

    @Nested
    class getProjectsTests {

        @Test
        void getProjects_filterByName() {

            Project match = Project.builder()
                    .name("Alpha")
                    .visibility(ProjectVisibility.PUBLIC)
                    .build();

            Project noMatch = Project.builder()
                    .name("Beta")
                    .visibility(ProjectVisibility.PUBLIC)
                    .build();

            when(projectRepository.findAll()).thenReturn(List.of(match, noMatch));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            List<ProjectDto> result = projectService.getProjects("Alpha", null, null);

            assertThat(result).hasSize(1);
            verify(projectMapper).toDto(match);
            verify(projectMapper, never()).toDto(noMatch);
        }

        @Test
        void getProjects_filterByStatus() {

            Project match = Project.builder()
                    .status(ProjectStatus.CREATED)
                    .visibility(ProjectVisibility.PUBLIC)
                    .build();

            Project noMatch = Project.builder()
                    .status(ProjectStatus.IN_PROGRESS)
                    .visibility(ProjectVisibility.PUBLIC)
                    .build();

            when(projectRepository.findAll()).thenReturn(List.of(match, noMatch));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            List<ProjectDto> result = projectService.getProjects(null, ProjectStatus.CREATED, null);

            assertThat(result).hasSize(1);
            verify(projectMapper).toDto(match);
            verify(projectMapper, never()).toDto(noMatch);
        }

        @Test
        void getProjects_filterByVisibility() {

            Project match = Project.builder()
                    .visibility(ProjectVisibility.PUBLIC)
                    .build();

            Project noMatch = Project.builder()
                    .visibility(ProjectVisibility.PRIVATE)
                    .build();

            when(projectRepository.findAll()).thenReturn(List.of(match, noMatch));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            List<ProjectDto> result = projectService.getProjects(null, null, 999L);

            assertThat(result).hasSize(1);
            verify(projectMapper).toDto(match);
            verify(projectMapper, never()).toDto(noMatch);
        }
    }

    @Nested
    class getProjectsByIdTests {

        @Test
        void getProjectsById_success() {

            Long projectId = 1L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            ProjectDto result = projectService.getProjectById(projectId);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(dto);
            verify(projectValidator).validateId(projectId);
            verify(projectRepository).findById(projectId);
        }

        @Test
        void getProjectsById_notFound_throws() {
            Long projectId = 99L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> projectService.getProjectById(projectId))
                    .isInstanceOf(ProjectNotFoundException.class);
        }
    }

    @Nested
    class getAllProjectsTests {

        @Test
        void getAllProjects_privateHiddenFromStranger() {

            Project publicProject = Project.builder()
                    .visibility(ProjectVisibility.PUBLIC)
                    .build();

            Project privateProject = Project.builder()
                    .ownerId(1L)
                    .visibility(ProjectVisibility.PRIVATE)
                    .build();

            when(projectRepository.findAll()).thenReturn(List.of(publicProject, privateProject));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            List<ProjectDto> result = projectService.getAllProjects(999L);

            assertThat(result).hasSize(1);
            verify(projectMapper).toDto(publicProject);
            verify(projectMapper, never()).toDto(privateProject);
        }

        @Test
        void getAllProjects_privateVisibleForOwner() {

            Project privateProject = Project.builder()
                    .ownerId(1L)
                    .visibility(ProjectVisibility.PRIVATE)
                    .build();

            when(projectRepository.findAll()).thenReturn(List.of(privateProject));
            when(projectMapper.toDto(any(Project.class))).thenReturn(dto);

            List<ProjectDto> result = projectService.getAllProjects(1L);

            assertThat(result).hasSize(1);
            verify(projectMapper).toDto(privateProject);
        }
    }
}
