package kg.devcats.internlabs.core.dto.response;

import kg.devcats.internlabs.core.dto.CoreStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {
    private CoreStatus result;
    private String message;
    private Object data;

    public CommonResponse(CoreStatus result, String message) {
        this.result = result;
        this.message = message;
    }
}
