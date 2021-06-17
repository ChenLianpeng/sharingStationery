package person.clp.stationeryback.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Record {
    Integer id;
    String type;
    Integer userId;
    Integer count;
    Integer way;
    String time;

}
