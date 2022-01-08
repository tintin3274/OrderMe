package th.ku.orderme.service;

import org.springframework.stereotype.Service;
import th.ku.orderme.repository.OptionalRepository;

@Service
public class OptionalService {
    private final OptionalRepository optionalRepository;

    public OptionalService(OptionalRepository optionalRepository) {
        this.optionalRepository = optionalRepository;
    }
}
