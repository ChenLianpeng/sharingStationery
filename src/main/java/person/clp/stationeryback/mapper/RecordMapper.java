package person.clp.stationeryback.mapper;

import org.apache.ibatis.annotations.Param;
import person.clp.stationeryback.entity.Record;

import java.util.List;

public interface RecordMapper {
    void insert(Record record);

    List<Record> getRecordByUserId(@Param("userId")Integer userId, @Param("way")Integer way);
}
