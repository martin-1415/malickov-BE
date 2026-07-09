package cz.malickov.backend.controller;

import cz.malickov.backend.entity.Holiday;
import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.service.UtilsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/*
 * Controller to get what ever is not connected to main classes
 */
@RestController
@RequestMapping("/api/utils")
public class UtilsController {
    private final UtilsService utilsService;

    public UtilsController(UtilsService utilsService) {
        this.utilsService = utilsService;
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/getFreeIdentifiers")
    public ResponseEntity<List<Identificator>> getFreeIdentifiers() {
        List<Identificator> freeIdentificators = this.utilsService.getFreeIdentificators();

        return ResponseEntity
                .ok()
                .body(freeIdentificators);
    }

    @GetMapping("/getHolidays")
    public ResponseEntity<List<Holiday>> getHolidays() {
        List<Holiday> holidays = this.utilsService.getHolidays();

        return ResponseEntity
                .ok()
                .body(holidays);
    }

    /*
     * test endpoint to ping the pod and the server
     */
    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity
                .ok()
                .body("Hello world");
    }

}
