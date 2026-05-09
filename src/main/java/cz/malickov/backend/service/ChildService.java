package cz.malickov.backend.service;

import cz.malickov.backend.dto.ChildInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.entity.Child;
import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.GeneralException;
import cz.malickov.backend.error.childExceptions.ChildNotFoundException;
import cz.malickov.backend.error.childExceptions.ParentNotFoundException;
import cz.malickov.backend.mapper.ChildMapper;
import cz.malickov.backend.repository.ChildRepository;
import cz.malickov.backend.repository.IdentificatorRepository;
import cz.malickov.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ChildService {
    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final UserRepository userRepository;
    private final IdentificatorRepository identificatorRepository;

    public ChildService(ChildRepository childRepository,
                        ChildMapper childMapper,
                        UserRepository userRepository,
                        IdentificatorRepository identificatorRepository) {
        this.childRepository = childRepository;
        this.childMapper = childMapper;
        this.userRepository = userRepository;
        this.identificatorRepository = identificatorRepository;

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
            Integer identificatorId= childDto.identificator().getIdentificatorId();
            Identificator identificator=this.identificatorRepository.findById(identificatorId)
                    .orElseThrow(() -> new GeneralException("Identificator with ID "+ identificatorId + " not found"));
            child.setIdentificator(identificator);
        }


        this.childRepository.save(child);
        return this.childMapper.toOutboundDTO(child);
    }

    public ChildOutboundDTO editChild(ChildInboundDTO childInboundDTO){
        Child child = this.childRepository.findByChildUuid(childInboundDTO.childUuid())
                .orElseThrow(() -> new ChildNotFoundException(childInboundDTO.childUuid()));
        Integer identificatorId=childInboundDTO.identificator().getIdentificatorId();

        Identificator identificator=this.identificatorRepository.findById(identificatorId)
                .orElseThrow(() -> new GeneralException("Identificator with ID "+ identificatorId + " not found"));

        this.childMapper.updateEntity(childInboundDTO,child);
        child.setIdentificator(identificator);

        this.childRepository.save(child);

        return this.childMapper.toOutboundDTO(child);
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
