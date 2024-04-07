package kg.devcats.internlabs.admin.dto.mapper;

import kg.devcats.internlabs.admin.dto.response.LimitDTO;
import kg.devcats.internlabs.core.entity.Limit;
import org.springframework.stereotype.Service;

import java.util.function.Function;
@Service
public class LimitDTOMapper implements Function<Limit, LimitDTO> {
    @Override
    public LimitDTO apply(Limit limit){
         return new LimitDTO(
                 limit.getId(),
                 limit.getPeriod(),
                 limit.getAmount()
         );
    }
}
