package org.example.tracker.service;

import org.example.tracker.ModelGenerate;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class BaseTest extends ModelGenerate {
    @Autowired
    ModelMapper modelMapper;
}