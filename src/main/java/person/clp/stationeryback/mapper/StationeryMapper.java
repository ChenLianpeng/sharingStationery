package person.clp.stationeryback.mapper;

import org.apache.ibatis.annotations.Param;
import person.clp.stationeryback.entity.Stationery;

import java.util.List;

public interface StationeryMapper {
    List<Stationery> getStationery();

    void updateStationery(@Param("type")String type,@Param("count")Integer count);
}
