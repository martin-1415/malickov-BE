package cz.malickov.backend.controller;


import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.entity.Identificator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/*
 * Controler to get what ever is not connected to main classes
 */
@RestController
@RequestMapping("/api/utils")
public class UtilsController {

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/getFreeIdentificators")
    public ResponseEntity<List<Identificator>> getFreeIdentificators() {
        List<Identificator> freeIdentificators = this.utilsService.getFreeIdentificators();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(freeIdentificators);
    }

}
