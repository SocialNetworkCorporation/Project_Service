package faang.school.projectservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private UserContext userContext;

    private ObjectMapper mapper = new ObjectMapper();

    private ProjectDto dto;

    @BeforeEach
    void setUp() {
        dto = ProjectDto.builder()
                .id(1L)
                .name("Test")
                .ownerId(1L)
                .build();
    }

    @Test
    void createProject_shouldReturnCreated() throws Exception {
        when(projectService.createProject(any(ProjectDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void getProjectById_shouldReturnProject() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void updateProject_shouldReturnUpdated() throws Exception {
        when(projectService.updateProject(eq(1L), any(ProjectDto.class)))
                .thenReturn(dto);

        mockMvc.perform(put("/api/projects/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void getProjects_shouldReturnList() throws Exception {
        when(projectService.getProjects(anyString(), any(), anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/projects")
                .param("name", "Test")
                .param("status", "CREATED")
                .param("requesterId", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test"));
    }

    @Test
    void getAllProjects_shouldReturnList() throws Exception {
        when(projectService.getAllProjects(anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/projects/all")
                        .param("requesterId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getProjectById_notFound_shouldReturn404() throws Exception {
        when(projectService.getProjectById(99L))
                .thenThrow(new ProjectNotFoundException("Project not found"));

        mockMvc.perform(get("/api/projects/{id}", 99L))
                .andExpect(status().isNotFound()) // Ожидаем 404
                .andExpect(jsonPath("$.message").value("Project not found")); // И JSON с ошибкой
    }
}
