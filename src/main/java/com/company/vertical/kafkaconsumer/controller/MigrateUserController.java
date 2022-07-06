package com.company.vertical.kafkaconsumer.controller;

import com.company.vertical.kafkaconsumer.service.MigrateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/migrate/user")
public class MigrateUserController {

    @Autowired
    MigrateUserService migrateUserService;

    @PostMapping("/{id}")
    public ResponseEntity findUserById(@PathVariable(value = "id") long id) {
        migrateUserService.migrateUserById(id);
        return ResponseEntity.accepted().build();
    }
}
