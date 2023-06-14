package org.example.tracker.dto.task;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResp extends TaskReq {

    @Schema(description = "уникальный идентификатор задачи",
            example = "32")
    private int id;

    @Schema(description = "Уникальный идентификатор автора задачи. " +
            "Автором задачи может являться только участник проекта.",
            example = "3")
    private int authorId;

    private TaskStatus status;

    @Schema(description = "Дата создания - дата когда задача была создана.",
            example = "2020-04-28T00:00:00.000Z")
    private Instant createdDatetime;

    @Schema(description = "Дата последнего изменения задачи - дата последнего редактирования задачи " +
            "(но не изменение статуса задачи).",
            example = "2020-04-28T00:00:00.000Z")
    private Instant updateDatetime;

    @ArraySchema(
            schema = @Schema(description = "URL файла привязанного к задаче.",
                    example = "http://localhost:8080/file/5b1375eb-3f60-4ad9-881b-910b0fe24dcc")
    )
    private List<String> files;
//            example = {@ExampleObject(value = "[\"A\"]")}
}
