package cz.malickov.backend.service;

import cz.malickov.backend.dto.ChildInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.entity.Child;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.childExceptions.ParentNotFoundException;
import cz.malickov.backend.mapper.ChildMapper;
import cz.malickov.backend.repository.ChildRepository;
import cz.malickov.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ChildService {
    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final UserRepository userRepository;
    public ChildService(ChildRepository childRepository, ChildMapper childMapper, UserRepository userRepository) {
        this.childRepository = childRepository;
        this.childMapper = childMapper;
        this.userRepository = userRepository;

    }

//    public List<Child> getChildrenByParentUuid(UUID userUuid){
//        return childRepository.findChildrenByParentUuid( userUuid );
//    }

    public ChildOutboundDTO createChild(ChildInboundDTO childDto){
        Child child = this.childMapper.toEntity(childDto);

        User user = userRepository.findByUserUuid(childDto.userUuid())
                .orElseThrow(() -> new ParentNotFoundException(childDto.userUuid().toString() ));
        child.setUser(user);

        Child returnedChild = this.childRepository.save(child);
        return this.childMapper.toOutboundDTO(returnedChild);
    }

}
