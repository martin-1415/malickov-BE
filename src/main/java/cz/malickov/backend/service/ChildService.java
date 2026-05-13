package cz.malickov.backend.service;

import cz.malickov.backend.dto.ChildInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.entity.Child;
import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.exception.childExceptions.ChildNotFoundException;
import cz.malickov.backend.exception.childExceptions.ParentNotFoundException;
import cz.malickov.backend.exception.utilsExceptions.IdentifierNotFoundException;
import cz.malickov.backend.mapper.ChildMapper;
import cz.malickov.backend.repository.ChildRepository;
import cz.malickov.backend.repository.IdentifierRepository;
import cz.malickov.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ChildService {
    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final UserRepository userRepository;
    private final IdentifierRepository identifierRepository;

    public ChildService(ChildRepository childRepository,
                        ChildMapper childMapper,
                        UserRepository userRepository,
                        IdentifierRepository identifierRepository) {
        this.childRepository = childRepository;
        this.childMapper = childMapper;
        this.userRepository = userRepository;
        this.identifierRepository = identifierRepository;

    }

//    public List<Child> getChildrenByParentUuid(UUID userUuid){
//        return childRepository.findChildrenByParentUuid( userUuid );
//    }

    public ChildOutboundDTO createChild(ChildInboundDTO childDto){
        Child child = this.childMapper.toEntity(childDto);

        User user = userRepository.findByUserUuid(childDto.userUuid())
                .orElseThrow(() -> new ParentNotFoundException(childDto.userUuid().toString() ));
        child.setUser(user);

        if( childDto.identificator() != null){
            int identifierId= childDto.identificator().getIdentificatorId();
            Identificator identificator=this.identifierRepository.findById(identifierId)
                    .orElseThrow(() -> new IdentifierNotFoundException( identifierId ));
            child.setIdentificator(identificator);
        }
        this.childRepository.save(child);
        return this.childMapper.toOutboundDTO(child);
    }


    @Transactional
    public ChildOutboundDTO editChild(ChildInboundDTO dto) {

        Child child = childRepository.findByChildUuid(dto.childUuid())
                .orElseThrow(() -> new ChildNotFoundException(dto.childUuid()));

        int identifierId = dto.identificator().getIdentificatorId();
        Identificator identificator = identifierRepository.findById(identifierId)
                .orElseThrow(() -> new IdentifierNotFoundException(identifierId));

        User parent = userRepository.findByUserUuid(dto.userUuid())
                .orElseThrow(() -> new ParentNotFoundException(dto.userUuid().toString()));

        // Updating mutable fields only, ignored fields in mapper
        childMapper.updateEntity(dto, child);

        child.setIdentificator(identificator);
        child.setUser(parent);

        return childMapper.toOutboundDTO(child);
    }


    public List<ChildOutboundDTO> getActiveChildren() {
        List<Child> activeChildren = this.childRepository.findByActiveTrueOrderByLastNameAsc();
        return activeChildren.stream()
                .map(childMapper::toOutboundDTO)
                .collect(Collectors.toList());
    }

    public List<ChildOutboundDTO> getInactiveChildren() {
        List<Child> activeChildren = this.childRepository.findByActiveFalseOrderByLastNameAsc();
        return activeChildren.stream()
                .map(childMapper::toOutboundDTO)
                .collect(Collectors.toList());
    }
}
