package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.LimitDTOMapper;
import kg.devcats.internlabs.admin.dto.response.LimitDTO;
import kg.devcats.internlabs.core.entity.Limit;
import kg.devcats.internlabs.core.repository.LimitRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LimitService {

    private LimitRepository limitRepository;
    private final LimitDTOMapper limitDTOMapper;

    public LimitService(LimitRepository limitRepository, LimitDTOMapper limitDTOMapper) {
        this.limitRepository = limitRepository;
        this.limitDTOMapper = limitDTOMapper;
    }


    public List<LimitDTO> getAllLimits() {
        return limitRepository.findAll()
                .stream()
                .map(limitDTOMapper)
                .collect(Collectors.toList());
    }

    public Optional<LimitDTO> getLimitById(Long id) {
        return limitRepository.findById(id).map(limitDTOMapper);
    }


    public Limit createLimit(Limit limit) {
        boolean existsByPeriod = limitRepository.existsByPeriod(limit.getPeriod());
        if (existsByPeriod) {
            throw new DataIntegrityViolationException("Limit with id '" + limit.getId() + "' already exists.");
        }
        return limitRepository.save(limit);
    }


    public Limit updateLimit(Long id, Limit updatedLimit) {
        Optional<Limit> existingLimit = limitRepository.findById(id);

        if (existingLimit.isPresent()) {
            Limit limit = existingLimit.get();
            limit.setPeriod(updatedLimit.getPeriod());
            limit.setAmount(updatedLimit.getAmount());

            return limitRepository.save(limit);
        } else {
            return null;
        }
    }

    public void deleteLimit(Long id) {
        limitRepository.deleteById(id);
    }
}
