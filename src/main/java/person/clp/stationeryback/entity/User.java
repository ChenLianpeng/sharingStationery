package person.clp.stationeryback.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class User {
    private Integer id;
    private String username;
    private String password;
    private String academe;
    private String realname;
    private String studentNumber;
    private Integer groupId;
    private String faceId;
    private byte[] faceFeature;
    private Integer similarValue;
}
