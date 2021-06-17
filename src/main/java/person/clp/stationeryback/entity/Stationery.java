package person.clp.stationeryback.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Stationery {
    Integer id;
    Integer eraser;
    Integer cartridge;
    Integer paper;
    Integer umbrella;
}
