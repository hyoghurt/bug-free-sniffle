package org.example.tracker.dto.task;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskReq {

    @Schema(description = "уникальный идентификатор проекта",
            example = "23")
    @NotNull(message = "projectId required")
    private Integer projectId;

    @Schema(description = "Наименование задачи - текстовое значение, отражающее краткую информацию о задачи.",
            example = "сделай, я сказал")
    @NotNull(message = "title required")
    @Size(max = 64, message = "1 <= title.length <= 64")
    private String title;

    @Schema(description = "Описание задачи - текстовое значение, содержащее детальное описание задачи.",
            example = "злая задача")
    @Size(max = 1024, message = "1 <= description.length <= 1024")
    private String description;

    @Schema(description = "Исполнитель задачи - сотрудник, которому необходимо исполнить задачу. " +
            "Можно выбрать исполнителя только участника проекта (сотрудник добавленный в команду проекта). " +
            "Назначить исполнителя можно только сотрудника в статусе Активный.",
            example = "42")
    private Integer assigneesId;

    @Schema(description = "Трудозатраты - оценка, сколько в часах необходимо на ее исполнение.",
            example = "3")
    @NotNull(message = "laborCostsInHours required")
    private Integer laborCostsInHours;

    //TODO change datetime -> date
    //TODO create validate
    @Schema(description = "Крайний срок - дата, когда задача должна быть исполнена. " +
            "Нельзя выбрать дату если дата меньше, чем  дата создания + трудозатраты.",
            example = "2020-04-28T00:00:00.000Z")
    @NotNull(message = "deadlineDatetime required")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
//    @JsonSerialize(using = InstantSerializer.class)
//    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant deadlineDatetime;
}
