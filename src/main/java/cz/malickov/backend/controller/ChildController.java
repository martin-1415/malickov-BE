package cz.malickov.backend.controller;

import cz.malickov.backend.dto.ChildInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.exception.childExceptions.ChildNotFoundException;
import cz.malickov.backend.service.ChildService;
import cz.malickov.backend.service.JWTService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/child")
public class ChildController {
    private final ChildService childService;
    private final JWTService jwtService;

    public ChildController(ChildService childService,
                           JWTService jwtService){
        this.childService = childService;
        this.jwtService = jwtService;
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/getActiveChildren")
    public ResponseEntity<List<ChildOutboundDTO>> getActiveChildren()
    {
        List<ChildOutboundDTO> activeChildren =
                this.childService.getActiveChildren();
        return ResponseEntity.ok()
                .body( activeChildren );
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/getInactiveChildren")
    public ResponseEntity<List<ChildOutboundDTO>> getInactiveChildren()
    {
        List<ChildOutboundDTO> inctiveChildren =
                this.childService.getInactiveChildren();
        return ResponseEntity.ok()
                .body( inctiveChildren );
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PostMapping("/newChild")
    public ResponseEntity<ChildOutboundDTO> createChild(@RequestBody @Valid ChildInboundDTO childInboundDTO) {
        ChildOutboundDTO savedChild = this.childService.createChild(childInboundDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedChild);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PutMapping("/editChild/{childUuid}")
    public ResponseEntity<ChildOutboundDTO> editChild(@PathVariable UUID childUuid, @RequestBody @Valid ChildInboundDTO childInboundDTO) {

        if (childInboundDTO.childUuid() != null && !childUuid.equals(childInboundDTO.childUuid())) {
            throw new ChildNotFoundException("Path id ("+ childUuid + ") and payload id ("+ childInboundDTO.childUuid()+") mismatch.");
        }

        ChildOutboundDTO savedChild = this.childService.editChild(childInboundDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(savedChild);
    }


    @GetMapping("/getActiveUserChildren")
    public ResponseEntity<List<ChildOutboundDTO>> getActiveChildrenByUserUUID(
            @CookieValue(value = "JWT") String token
    ) {
        UUID userUuid = jwtService.extractUserUuid(token);
        List<ChildOutboundDTO> children = this.childService.getActiveChildrenByUserUuid(userUuid);
        return ResponseEntity.ok().body(children);
    }

    @GetMapping("/getInactiveUserChildren")
    public ResponseEntity<List<ChildOutboundDTO>> getInactiveChildrenByUserUUID(
            @CookieValue(value = "JWT") String token
    ) {
        UUID userUuid = jwtService.extractUserUuid(token);
        List<ChildOutboundDTO> children = this.childService.getInactiveChildrenByUserUuid(userUuid);
        return ResponseEntity.ok().body(children);
    }

}
