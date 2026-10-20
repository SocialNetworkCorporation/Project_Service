package faang.school.projectservice.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

@JsonProperty(access = JsonProperty.Access.READ_ONLY)
private Long id;

private String name;

private String description;

private Long ownerId;

@JsonProperty(access = JsonProperty.Access.READ_ONLY)
private LocalDateTime updatedAt;

private ProjectStatus status;

private ProjectVisibility visibility;
}
