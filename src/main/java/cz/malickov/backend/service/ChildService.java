package cz.malickov.backend.service;

import cz.malickov.backend.entity.Child;
import cz.malickov.backend.repository.ChildRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChildService {
    private final ChildRepository childRepository;

    public ChildService(ChildRepository childRepository) {
        this.childRepository = childRepository;
        log.debug("Child repository initialized");
    }

    public List<Child> getChildrenByParentId(long userId){
        return childRepository.findChildrenByParentId( userId );
    }

}
