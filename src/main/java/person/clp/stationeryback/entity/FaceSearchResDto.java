package person.clp.stationeryback.entity;

import lombok.*;

/**
 * @author Jerry
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FaceSearchResDto {
    private String faceId;
    private String username;
    private Integer similarValue;
    private Integer age;
    private String gender;
    private String image;
}
