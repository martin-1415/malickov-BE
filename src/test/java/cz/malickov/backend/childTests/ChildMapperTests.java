package cz.malickov.backend.childTests;

import cz.malickov.backend.dto.ChildInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.entity.Child;
import cz.malickov.backend.enums.Department;
import cz.malickov.backend.mapper.ChildMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
class ChildMapperTest {

    @Autowired
    private ChildMapper mapper;

    @Test
    void shouldMapToEntity_fromInboundDTO() {
        // given
        UUID childId = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();
        Date birthDay = new Date();

        ChildInboundDTO dto = new ChildInboundDTO(
                childId,
                "John",
                "Doe",
                Department.NURSERY1,
                birthDay,
                true,
                "note",
                userUuid,
                null,
                true, false, true, false, true
        );

        // when
        Child entity = mapper.toEntity(dto);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getDepartment()).isEqualTo(Department.KINDERGARTEN);
        assertThat(entity.getBirthDay()).isEqualTo(birthDay);
        assertThat(entity.getActive()).isTrue();
        assertThat(entity.getNotes()).isEqualTo("note");

        // booleans
        assertThat(entity.getMon()).isTrue();
        assertThat(entity.getTue()).isFalse();
        assertThat(entity.getWed()).isTrue();
        assertThat(entity.getThu()).isFalse();
        assertThat(entity.getFri()).isTrue();

        // ignored fields
        assertThat(entity.getChildUuid()).isNull(); // ignored in mapper
        assertThat(entity.getUser()).isNull();    // not mapped automatically
    }

    @Test
    void shouldUpdateEntity_ignoreNulls() {
        // given
        Child existing = Child.builder()
                .firstName("Old")
                .lastName("Name")
                .department(Department.NURSERY1)
                .active(true)
                .notes("old note")
                .mon(true)
                .build();

        ChildInboundDTO dto = new ChildInboundDTO(
                UUID.randomUUID(),
                "New",
                null, // should NOT overwrite
                null, // should NOT overwrite
                null,
                false,
                null,
                UUID.randomUUID(),
                null,
                null, null, null, null, null
        );

        // when
        mapper.updateEntity(dto, existing);

        // then
        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Name"); // unchanged
        assertThat(existing.getDepartment()).isEqualTo(Department.NURSERY2); // unchanged
        assertThat(existing.getActive()).isFalse(); // updated
        assertThat(existing.getNotes()).isEqualTo("old note"); // unchanged
        assertThat(existing.getMon()).isTrue(); // unchanged
    }

    @Test
    void shouldMapToOutboundDTO() {
        // given
        UUID childUuid = UUID.randomUUID();

        Child entity = Child.builder()
                .childUuid(childUuid)
                .firstName("Jane")
                .lastName("Doe")
                .department(Department.NURSERY1)
                .birthDay(new Date())
                .active(true)
                .notes("some note")
                .mon(true)
                .tue(false)
                .wed(true)
                .thu(false)
                .fri(true)
                .build();

        // when
        ChildOutboundDTO dto = mapper.toOutboundDTO(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.childUuid()).isEqualTo(childUuid);
        assertThat(dto.firstName()).isEqualTo("Jane");
        assertThat(dto.lastName()).isEqualTo("Doe");
        assertThat(dto.department()).isEqualTo(Department.NURSERY1);
        assertThat(dto.active()).isTrue();
        assertThat(dto.notes()).isEqualTo("some note");

        // booleans
        assertThat(dto.mon()).isTrue();
        assertThat(dto.tue()).isFalse();
        assertThat(dto.wed()).isTrue();
        assertThat(dto.thu()).isFalse();
        assertThat(dto.fri()).isTrue();
    }
}