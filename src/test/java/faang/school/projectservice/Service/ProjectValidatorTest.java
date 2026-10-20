package faang.school.projectservice.Service;

import faang.school.projectservice.Validation.ProjectValidator;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.exception.ProjectValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    private ProjectDto dto;

    @BeforeEach
    void setup(){
        dto = ProjectDto.builder()
                .name("Valid Name")
                .ownerId(1L)
                .build();
    }

    @Nested
    class validateForCreateTests {

        @Test
        void validateForCreate_success() {

            when(projectRepository.existsByOwnerIdAndName(
                    1L, "Valid Name")).thenReturn(false);

            assertThatCode(() -> projectValidator.validateForCreate(dto)).doesNotThrowAnyException();
        }

        @Test
        void validateForCreate_nullDto_Throws() {

            assertThatThrownBy(() -> projectValidator.validateForCreate(null))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForCreate_nullName_Throws() {

            dto.setName(null);

            assertThatThrownBy(() -> projectValidator.validateForCreate(dto))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForCreate_blankName_Throws() {

            dto.setName(" ");

            assertThatThrownBy(() -> projectValidator.validateForCreate(dto))
                    .isInstanceOf(ProjectValidationException.class);

        }

        @Test
        void validateForCreate_tooLongName_Throws() {

            dto.setName("a".repeat(129));

            assertThatThrownBy(() -> projectValidator.validateForCreate(dto))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForCreate_tooLongDescription_Throws() {

            dto.setName("a".repeat(4097));

            assertThatThrownBy(() -> projectValidator.validateForCreate(dto))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForCreate_nullOwnerId_Throws() {

            dto.setOwnerId(null);

            assertThatThrownBy(() -> projectValidator.validateForCreate(dto))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForCreate_duplicate_Throws() {

            when(projectRepository.existsByOwnerIdAndName(1L, "Valid Name")).thenReturn(true);

            assertThatThrownBy(() -> projectValidator.validateForCreate(dto))
                    .isInstanceOf(ProjectValidationException.class);
        }
    }

    @Nested
    class validateForUpdateTests {

        @Test
        void validateForUpdate_success() {

            ProjectDto emptyDto = new ProjectDto();

            assertThatCode(() -> projectValidator.validateForUpdate(1L, emptyDto))
                    .doesNotThrowAnyException();
        }

        @Test
        void validateForUpdate_nullId_Throws() {

            assertThatThrownBy(() -> projectValidator.validateForUpdate(null, dto))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForUpdate_nullDto_Throws() {

            assertThatThrownBy(() -> projectValidator.validateForUpdate(1L, null))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForUpdate_tooLongName_Throws() {

            dto.setName("a".repeat(129));

            assertThatThrownBy(() -> projectValidator.validateForUpdate(1L, dto))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForUpdate_tooLongDescription_Throws() {
            dto.setName("a".repeat(4097));

            assertThatThrownBy(() -> projectValidator.validateForUpdate(1L, dto))
                    .isInstanceOf(ProjectValidationException.class);
        }

        @Test
        void validateForUpdate_blankName_Throws() {

            dto.setName(" ");

            assertThatThrownBy(() -> projectValidator.validateForUpdate(1L, dto))
                    .isInstanceOf(ProjectValidationException.class);
        }
    }

    @Nested
    class validateIdTests {

        @Test
        void validateId_success() {

            assertThatCode(() -> projectValidator.validateId(1L))
                    .doesNotThrowAnyException();
        }

        @Test
        void validateId_nullId_Throws() {

            assertThatThrownBy(() -> projectValidator.validateId(null))
                    .isInstanceOf(ProjectValidationException.class);
        }
    }
}
