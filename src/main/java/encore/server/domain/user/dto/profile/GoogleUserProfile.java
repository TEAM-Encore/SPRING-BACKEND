package encore.server.domain.user.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleUserProfile {
  private String sub;
  private String email;
  private String name;
}
