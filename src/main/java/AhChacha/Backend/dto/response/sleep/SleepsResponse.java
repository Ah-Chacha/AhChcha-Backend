package AhChacha.Backend.dto.response.sleep;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SleepsResponse {
    List<SleepResponse> sleepResponses;
}
