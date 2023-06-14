package org.example.tracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.tracker.dto.error.ErrorResp;
import org.example.tracker.service.FileService;
import org.example.tracker.service.TaskService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.UUID;

@Controller
@Tag(name = "file", description = "управление файлами")
@SecurityRequirement(name = "basicScheme")
@ApiResponse(responseCode = "401", content = @Content)
@RequestMapping("/files")
public class FileController {
    private final TaskService taskService;
    private final FileService fileService;

    public FileController(TaskService taskService, FileService fileService) {
        this.taskService = taskService;
        this.fileService = fileService;
        fileService.setIdToUrlFunction(FileController::getUrl);
    }

    @Operation(summary = "сохранение",
            description = "Сохранить файл для задачи.",
            responses = {
                    @ApiResponse(responseCode = "201",
                            content = @Content(schema = @Schema(example = "e00092b5-462e-4bf4-b202-8cb59460bf65"))),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public String addFile(@RequestPart(value = "file") MultipartFile file,
                          @Parameter(description = "уникальный идентификор задачи", example = "1")
                          @RequestParam Integer taskId) {
        return taskService.addFile(taskId, file);
    }

    @Operation(summary = "получение",
            description = "Получить файл.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@Parameter(
            description = "уникальный идентификатор файла",
            example = "5b1375eb-3f60-4ad9-881b-910b0fe24dcc")
                                            @PathVariable UUID id) {
        return fileService.get(id);
    }

    public static String getUrl(UUID id) {
        return MvcUriComponentsBuilder
                .fromMethodName(FileController.class, "getFile", id.toString())
                .build()
                .toString();
    }
}
