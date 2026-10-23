package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto dto) {
        ProjectDto created = projectService.createProject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id,
                                                    @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(projectService.updateProject(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectsById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false)ProjectStatus status,
            @RequestParam(required = false) Long requesterId) {
        return ResponseEntity.ok(projectService.getProjects(name, status, requesterId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto>> getAllProjects(
            @RequestParam(required = false) Long requesterId) {
        return ResponseEntity.ok(projectService.getAllProjects(requesterId));
    }
}
