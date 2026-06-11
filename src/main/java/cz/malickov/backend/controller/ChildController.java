package cz.malickov.backend.controller;

import cz.malickov.backend.dto.ChildInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.enums.Role;
import cz.malickov.backend.exception.childExceptions.ChildUuidMismatchException;
import cz.malickov.backend.model.CustomUserDetails;
import cz.malickov.backend.service.ChildService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/child")
public class ChildController {
    private final ChildService childService;

    public ChildController(ChildService childService){
        this.childService = childService;
    }

    @Deprecated
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/getActiveChildren")
    public ResponseEntity<List<ChildOutboundDTO>> getActiveChildren()
    {
        List<ChildOutboundDTO> activeChildren =
                this.childService.getActiveChildren();
        return ResponseEntity.ok()
                .body( activeChildren );
    }

    @Deprecated
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/getInactiveChildren")
    public ResponseEntity<List<ChildOutboundDTO>> getInactiveChildren()
    {
        List<ChildOutboundDTO> inactiveChildren =
                this.childService.getInactiveChildren();
        return ResponseEntity.ok()
                .body( inactiveChildren );
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
            throw new ChildUuidMismatchException(childUuid, childInboundDTO.childUuid());
        }

        ChildOutboundDTO savedChild = this.childService.editChild(childInboundDTO);

        return ResponseEntity
                .ok()
                .body(savedChild);
    }

    /*
     *  For teachers and higher controller returns all active children
     * For a parent only his children
     */
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','TEACHER','PARENT')")
    @GetMapping("/getUserActiveChildren")
    public ResponseEntity<List<ChildOutboundDTO>> getActiveChildrenByUserUUID(
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        List<ChildOutboundDTO> children;

        if(Arrays.asList(Role.TEACHER, Role.MANAGER,Role.DIRECTOR).contains(userDetails.getRole()) ){
            children = this.childService.getActiveChildren();
        }else {
            children = this.childService.getActiveChildrenByUserUuid(userDetails.getUserUuid());
        }
        return ResponseEntity.ok().body(children);
    }

    /*
     *  For teachers and higher controller returns all inctive children
     * For a parent only his inactive children
     */
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','TEACHER','PARENT')")
    @GetMapping("/getUserInactiveChildren")
    public ResponseEntity<List<ChildOutboundDTO>> getInactiveChildrenByUserUUID(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ChildOutboundDTO> children;

        if(Arrays.asList(Role.TEACHER, Role.MANAGER,Role.DIRECTOR).contains(userDetails.getRole()) ){
            children = this.childService.getInactiveChildren();
        }else {
            children = this.childService.getInactiveChildrenByUserUuid(userDetails.getUserUuid());
        }
        return ResponseEntity.ok().body(children);
    }

}
