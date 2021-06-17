package person.clp.stationeryback.entity;

import lombok.*;

/**
 * @author Jerry
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceUserInfo extends User {
    private String faceId;
    private String username;
    private Integer similarValue;
    private byte[] faceFeature;
}
