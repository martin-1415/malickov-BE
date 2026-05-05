package cz.malickov.backend.controller;

import cz.malickov.backend.dto.ChildInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.service.ChildService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/child")
public class ChildController {
    private final ChildService childService;

    public ChildController(ChildService childService){
        this.childService = childService;
    }


    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PostMapping("/newChild")
    public ResponseEntity<ChildOutboundDTO> createChild(@RequestBody @Valid ChildInboundDTO childInboundDTO) {
        ChildOutboundDTO savedChild = this.childService.createChild(childInboundDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedChild);
    }

}
