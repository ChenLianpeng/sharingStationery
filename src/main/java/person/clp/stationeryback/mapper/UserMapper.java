package person.clp.stationeryback.mapper;

import org.apache.ibatis.annotations.Param;
import person.clp.stationeryback.entity.FaceUserInfo;
import person.clp.stationeryback.entity.User;

import java.util.List;

public interface UserMapper {
    User selectByName(String username);

    int insert(User user);

    List<FaceUserInfo> getUserFaceInfoByGroupId(@Param("groupId")Integer groupId);
}
